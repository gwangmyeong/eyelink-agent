package com.m2u.eyelink.collector.receiver.tcp;

import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.codahale.metrics.MetricRegistry;
import com.m2u.eyelink.collector.cluster.zookeeper.ZookeeperClusterService;
import com.m2u.eyelink.collector.config.CollectorConfiguration;
import com.m2u.eyelink.collector.monitor.MonitoredExecutorService;
import com.m2u.eyelink.collector.receiver.DispatchHandler;
import com.m2u.eyelink.collector.rpc.handler.AgentEventHandler;
import com.m2u.eyelink.collector.rpc.handler.AgentLifeCycleHandler;
import com.m2u.eyelink.collector.server.util.AgentLifeCycleState;
import com.m2u.eyelink.collector.util.AgentEventType;
import com.m2u.eyelink.collector.util.ExecutorFactory;
import com.m2u.eyelink.collector.util.MapUtils;
import com.m2u.eyelink.collector.util.PacketUtils;
import com.m2u.eyelink.context.HandshakePropertyType;
import com.m2u.eyelink.rpc.ELAgentSocket;
import com.m2u.eyelink.rpc.RequestPacket;
import com.m2u.eyelink.rpc.packet.HandshakeResponseCode;
import com.m2u.eyelink.rpc.packet.HandshakeResponseType;
import com.m2u.eyelink.rpc.packet.PingPacket;
import com.m2u.eyelink.rpc.packet.SendPacket;
import com.m2u.eyelink.rpc.server.ELAgentServer;
import com.m2u.eyelink.rpc.server.ELAgentServerAcceptor;
import com.m2u.eyelink.rpc.server.ServerMessageListener;
import com.m2u.eyelink.rpc.server.ServerStateChangeEventHandler;
import com.m2u.eyelink.thrift.DeserializerFactory;
import com.m2u.eyelink.thrift.HeaderTBaseDeserializer;
import com.m2u.eyelink.thrift.HeaderTBaseDeserializerFactory;
import com.m2u.eyelink.thrift.HeaderTBaseSerializer;
import com.m2u.eyelink.thrift.HeaderTBaseSerializerFactory;
import com.m2u.eyelink.thrift.SerializationUtils;
import com.m2u.eyelink.thrift.SerializerFactory;
import com.m2u.eyelink.thrift.ThreadLocalHeaderTBaseDeserializerFactory;
import com.m2u.eyelink.thrift.ThreadLocalHeaderTBaseSerializerFactory;
import com.m2u.eyelink.util.ELAgentThreadFactory;

public class TCPReceiver {

    private final Logger logger = LoggerFactory.getLogger(TCPReceiver.class);

    private final ThreadFactory tcpWorkerThreadFactory = new ELAgentThreadFactory("ELAgent-TCP-Worker");
    private final DispatchHandler dispatchHandler;
    private final ELAgentServerAcceptor serverAcceptor;

    private final CollectorConfiguration configuration;

    private final ZookeeperClusterService clusterService;

    private ExecutorService worker;

    private final SerializerFactory<HeaderTBaseSerializer> serializerFactory = new ThreadLocalHeaderTBaseSerializerFactory<>(new HeaderTBaseSerializerFactory(true, HeaderTBaseSerializerFactory.DEFAULT_UDP_STREAM_MAX_SIZE));
    private final DeserializerFactory<HeaderTBaseDeserializer> deserializerFactory = new ThreadLocalHeaderTBaseDeserializerFactory<>(new HeaderTBaseDeserializerFactory());

    @Autowired(required=false)
    private MetricRegistry metricRegistry;

    @Resource(name="agentEventWorker")
    private ExecutorService agentEventWorker;
    
    @Resource(name="agentEventHandler")
    private AgentEventHandler agentEventHandler;
    
    @Resource(name="agentLifeCycleHandler")
    private AgentLifeCycleHandler agentLifeCycleHandler;
    
    @Resource(name="channelStateChangeEventHandlers")
    private List<ServerStateChangeEventHandler> channelStateChangeEventHandlers = Collections.emptyList();

    public TCPReceiver(CollectorConfiguration configuration, DispatchHandler dispatchHandler) {
        this(configuration, dispatchHandler, new ELAgentServerAcceptor(), null);
    }

    public TCPReceiver(CollectorConfiguration configuration, DispatchHandler dispatchHandler, ELAgentServerAcceptor serverAcceptor, ZookeeperClusterService service) {
        if (configuration == null) {
            throw new NullPointerException("collector configuration must not be null");
        }
        if (dispatchHandler == null) {
            throw new NullPointerException("dispatchHandler must not be null");
        }
        
        this.dispatchHandler = dispatchHandler;
        this.configuration = configuration;
        this.serverAcceptor = serverAcceptor;
        this.clusterService = service;
    }

    public void afterPropertiesSet() {
        ExecutorService worker = ExecutorFactory.newFixedThreadPool(configuration.getTcpWorkerThread(), configuration.getTcpWorkerQueueSize(), tcpWorkerThreadFactory);
        if (configuration.isTcpWorkerMonitor()) {
            if (metricRegistry == null) {
                logger.warn("metricRegistry not autowired. Can't enable monitoring.");
                this.worker = worker;
            } else {
                this.worker = new MonitoredExecutorService(worker, metricRegistry, this.getClass().getSimpleName() + "-Worker");
            }
        } else {
            this.worker = worker;
        }

        if (clusterService != null && clusterService.isEnable()) {
            this.serverAcceptor.addStateChangeEventHandler(clusterService.getChannelStateChangeEventHandler());
        }

        for (ServerStateChangeEventHandler channelStateChangeEventHandler : this.channelStateChangeEventHandlers) {
            serverAcceptor.addStateChangeEventHandler(channelStateChangeEventHandler);
        }

        setL4TcpChannel(serverAcceptor, configuration.getL4IpList());
    }
    
    private void setL4TcpChannel(ELAgentServerAcceptor serverFactory, List<String> l4ipList) {
        if (l4ipList == null) {
            return;
        }
        try {
            List<InetAddress> inetAddressList = new ArrayList<>();
            for (int i = 0; i < l4ipList.size(); i++) {
                String l4Ip = l4ipList.get(i);
                if (StringUtils.isBlank(l4Ip)) {
                    continue;
                }

                InetAddress address = InetAddress.getByName(l4Ip);
                if (address != null) {
                    inetAddressList.add(address);
                }
            }
            
            InetAddress[] inetAddressArray = new InetAddress[inetAddressList.size()];
            serverFactory.setIgnoreAddressList(inetAddressList.toArray(inetAddressArray));
        } catch (UnknownHostException e) {
            logger.warn("l4ipList error {}", l4ipList, e);
        }
    }

    @PostConstruct
    public void start() {
        afterPropertiesSet();
        // take care when attaching message handlers as events are generated from the IO thread.
        // pass them to a separate queue and handle them in a different thread.
        this.serverAcceptor.setMessageListener(new ServerMessageListener() {

            @Override
            public HandshakeResponseCode handleHandshake(Map properties) {
                if (properties == null) {
                    return HandshakeResponseType.ProtocolError.PROTOCOL_ERROR;
                }

                boolean hasRequiredKeys = HandshakePropertyType.hasRequiredKeys(properties);
                if (!hasRequiredKeys) {
                    return HandshakeResponseType.PropertyError.PROPERTY_ERROR;
                }

                boolean supportServer = MapUtils.getBoolean(properties, HandshakePropertyType.SUPPORT_SERVER.getName(), true);
                if (supportServer) {
                    return HandshakeResponseType.Success.DUPLEX_COMMUNICATION;
                } else {
                    return HandshakeResponseType.Success.SIMPLEX_COMMUNICATION;
                }
            }

            @Override
            public void handleSend(SendPacket sendPacket, ELAgentSocket pinpointSocket) {
                receive(sendPacket, pinpointSocket);
            }

            @Override
            public void handleRequest(RequestPacket requestPacket, ELAgentSocket pinpointSocket) {
                requestResponse(requestPacket, pinpointSocket);
            }

            @Override
            public void handlePing(PingPacket pingPacket, ELAgentServer pinpointServer) {
                recordPing(pingPacket, pinpointServer);
            }
        });
        this.serverAcceptor.bind(configuration.getTcpListenIp(), configuration.getTcpListenPort());
    }

    private void receive(SendPacket sendPacket, ELAgentSocket pinpointSocket) {
        try {
            worker.execute(new Dispatch(sendPacket.getPayload(), pinpointSocket.getRemoteAddress()));
        } catch (RejectedExecutionException e) {
            // cause is clear - full stack trace not necessary 
            logger.warn("RejectedExecutionException Caused:{}", e.getMessage());
        }
    }

    private void requestResponse(RequestPacket requestPacket, ELAgentSocket pinpointSocket) {
        try {
            worker.execute(new RequestResponseDispatch(requestPacket, pinpointSocket));
        } catch (RejectedExecutionException e) {
            // cause is clear - full stack trace not necessary
            logger.warn("RejectedExecutionException Caused:{}", e.getMessage());
        }
    }
    
    private void recordPing(PingPacket pingPacket, ELAgentServer pinpointServer) {
        final int eventCounter = pingPacket.getPingId();
        long pingTimestamp = System.currentTimeMillis();
        try {
            if (!(eventCounter < 0)) {
                agentLifeCycleHandler.handleLifeCycleEvent(pinpointServer, pingTimestamp, AgentLifeCycleState.RUNNING, eventCounter);
            }
            agentEventHandler.handleEvent(pinpointServer, pingTimestamp, AgentEventType.AGENT_PING);
        } catch (Exception e) {
            logger.warn("Error handling ping event", e);
        }
    }

    private class Dispatch implements Runnable {
        private final byte[] bytes;
        private final SocketAddress remoteAddress;

        private Dispatch(byte[] bytes, SocketAddress remoteAddress) {
            if (bytes == null) {
                throw new NullPointerException("bytes");
            }
            this.bytes = bytes;
            this.remoteAddress = remoteAddress;
        }

        @Override
        public void run() {
            try {
                TBase<?, ?> tBase = SerializationUtils.deserialize(bytes, deserializerFactory);
                dispatchHandler.dispatchSendMessage(tBase);
            } catch (TException e) {
                if (logger.isWarnEnabled()) {
                    logger.warn("packet serialize error. SendSocketAddress:{} Cause:{}", remoteAddress, e.getMessage(), e);
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("packet dump hex:{}", PacketUtils.dumpByteArray(bytes));
                }
            } catch (Exception e) {
                // there are cases where invalid headers are received
                if (logger.isWarnEnabled()) {
                    logger.warn("Unexpected error. SendSocketAddress:{} Cause:{}", remoteAddress, e.getMessage(), e);
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("packet dump hex:{}", PacketUtils.dumpByteArray(bytes));
                }
            }
        }
    }

    private class RequestResponseDispatch implements Runnable {
        private final RequestPacket requestPacket;
        private final ELAgentSocket pinpointSocket;


        private RequestResponseDispatch(RequestPacket requestPacket, ELAgentSocket pinpointSocket) {
            if (requestPacket == null) {
                throw new NullPointerException("requestPacket");
            }
            this.requestPacket = requestPacket;
            this.pinpointSocket = pinpointSocket;
        }

        @Override
        public void run() {

            byte[] bytes = requestPacket.getPayload();
            SocketAddress remoteAddress = pinpointSocket.getRemoteAddress();
            try {
                TBase<?, ?> tBase = SerializationUtils.deserialize(bytes, deserializerFactory);
                TBase result = dispatchHandler.dispatchRequestMessage(tBase);
                if (result != null) {
                    byte[] resultBytes = SerializationUtils.serialize(result, serializerFactory);
                    pinpointSocket.response(requestPacket, resultBytes);
                }
            } catch (TException e) {
                if (logger.isWarnEnabled()) {
                    logger.warn("packet serialize error. SendSocketAddress:{} Cause:{}", remoteAddress, e.getMessage(), e);
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("packet dump hex:{}", PacketUtils.dumpByteArray(bytes));
                }
            } catch (Exception e) {
                // there are cases where invalid headers are received
                if (logger.isWarnEnabled()) {
                    logger.warn("Unexpected error. SendSocketAddress:{} Cause:{}", remoteAddress, e.getMessage(), e);
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("packet dump hex:{}", PacketUtils.dumpByteArray(bytes));
                }
            }
        }
    }

    @PreDestroy
    public void stop() {
        logger.info("ELAgent-TCP-Server stop");
        serverAcceptor.close();
        shutdownExecutor(worker);
        shutdownExecutor(agentEventWorker);
    }
    
    private void shutdownExecutor(ExecutorService executor) {
        if (executor == null) {
            return;
        }
        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
