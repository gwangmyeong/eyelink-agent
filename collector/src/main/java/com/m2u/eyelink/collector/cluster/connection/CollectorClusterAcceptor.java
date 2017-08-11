package com.m2u.eyelink.collector.cluster.connection;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.rpc.ClusterOption;
import com.m2u.eyelink.rpc.ELAgentSocket;
import com.m2u.eyelink.rpc.MessageListener;
import com.m2u.eyelink.rpc.RequestPacket;
import com.m2u.eyelink.rpc.SocketStateCode;
import com.m2u.eyelink.rpc.packet.HandshakeResponseCode;
import com.m2u.eyelink.rpc.packet.PingPacket;
import com.m2u.eyelink.rpc.packet.SendPacket;
import com.m2u.eyelink.rpc.server.ELAgentServer;
import com.m2u.eyelink.rpc.server.ELAgentServerAcceptor;
import com.m2u.eyelink.rpc.server.ServerMessageListener;
import com.m2u.eyelink.rpc.server.ServerStateChangeEventHandler;
import com.m2u.eyelink.sender.Role;
import com.m2u.eyelink.util.ClassUtils;

public class CollectorClusterAcceptor implements CollectorClusterConnectionProvider {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final InetSocketAddress bindAddress;
    private final CollectorClusterConnectionRepository clusterSocketRepository;

    private ELAgentServerAcceptor serverAcceptor;

    private final CollectorClusterConnectionOption option;

    public CollectorClusterAcceptor(CollectorClusterConnectionOption option, InetSocketAddress bindAddress, CollectorClusterConnectionRepository clusterSocketRepository) {
        this.option = option;
        this.bindAddress = bindAddress;
        this.clusterSocketRepository = clusterSocketRepository;
    }

    @Override
    public void start() {
        logger.info("{} initialization started.", ClassUtils.simpleClassName(this));

        ClusterOption clusterOption = new ClusterOption(true, option.getClusterId(), Role.ROUTER);

        ELAgentServerAcceptor serverAcceptor = new ELAgentServerAcceptor(clusterOption);
        serverAcceptor.setMessageListener(new ClusterServerMessageListener(option.getClusterId(), option.getRouteMessageHandler()));
        serverAcceptor.setServerStreamChannelMessageListener(option.getRouteStreamMessageHandler());
        serverAcceptor.addStateChangeEventHandler(new WebClusterServerChannelStateChangeHandler());
        serverAcceptor.bind(bindAddress);

        this.serverAcceptor = serverAcceptor;

        logger.info("{} initialization completed.", ClassUtils.simpleClassName(this));
    }

    @Override
    public void stop() {
        logger.info("{} destroying started.", ClassUtils.simpleClassName(this));

        if (serverAcceptor != null) {
            serverAcceptor.close();
        }

        logger.info("{} destroying completed.", ClassUtils.simpleClassName(this));
    }

    class ClusterServerMessageListener implements ServerMessageListener {

        private final String clusterId;
        private final MessageListener routeMessageListener;

        public ClusterServerMessageListener(String clusterId, MessageListener routeMessageListene) {
            this.clusterId = clusterId;
            this.routeMessageListener = routeMessageListene;
        }

        @Override
        public void handleSend(SendPacket sendPacket, ELAgentSocket pinpointSocket) {
            logger.info("handleSend packet:{}, remote:{}", sendPacket, pinpointSocket.getRemoteAddress());
        }

        @Override
        public void handleRequest(RequestPacket requestPacket, ELAgentSocket pinpointSocket) {
            logger.info("handleRequest packet:{}, remote:{}", requestPacket, pinpointSocket.getRemoteAddress());

            // TODO : need handle control message (looks like getClusterId, ..)
            routeMessageListener.handleRequest(requestPacket, pinpointSocket);
        }

        @Override
        public HandshakeResponseCode handleHandshake(Map properties) {
            logger.info("handle handShake {}", properties);
            return HandshakeResponseCode.DUPLEX_COMMUNICATION;
        }

        @Override
        public void handlePing(PingPacket pingPacket, ELAgentServer pinpointServer) {
            logger.info("handle ping {}", pingPacket);
        }

    }

    class WebClusterServerChannelStateChangeHandler implements ServerStateChangeEventHandler {

        @Override
        public void eventPerformed(ELAgentServer pinpointServer, SocketStateCode stateCode) throws Exception {
            if (stateCode.isRunDuplex()) {
                SocketAddress remoteAddress = pinpointServer.getRemoteAddress();
                clusterSocketRepository.putIfAbsent(remoteAddress, pinpointServer);
                return;
            } else if (stateCode.isClosed()) {
                SocketAddress remoteAddress = pinpointServer.getRemoteAddress();
                clusterSocketRepository.remove(remoteAddress);
                return;
            }
        }

        @Override
        public void exceptionCaught(ELAgentServer pinpointServer, SocketStateCode stateCode, Throwable e) {

        }
    }

}
