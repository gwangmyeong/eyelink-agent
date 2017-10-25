package com.m2u.eyelink.collector.cluster;

import javax.annotation.PreDestroy;

import org.apache.thrift.TBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.m2u.eyelink.collector.cluster.route.DefaultRouteHandler;
import com.m2u.eyelink.collector.cluster.route.RequestEvent;
import com.m2u.eyelink.collector.cluster.route.StreamEvent;
import com.m2u.eyelink.collector.cluster.route.StreamRouteHandler;
import com.m2u.eyelink.context.thrift.DeserializerFactory;
import com.m2u.eyelink.context.thrift.HeaderTBaseDeserializer;
import com.m2u.eyelink.context.thrift.HeaderTBaseSerializer;
import com.m2u.eyelink.context.thrift.SerializationUtils;
import com.m2u.eyelink.context.thrift.SerializerFactory;
import com.m2u.eyelink.context.thrift.TCommandTransfer;
import com.m2u.eyelink.context.thrift.TCommandTransferResponse;
import com.m2u.eyelink.context.thrift.TResult;
import com.m2u.eyelink.context.thrift.TRouteResult;
import com.m2u.eyelink.rpc.ELAgentSocket;
import com.m2u.eyelink.rpc.MessageListener;
import com.m2u.eyelink.rpc.RequestPacket;
import com.m2u.eyelink.rpc.ServerStreamChannelMessageListener;
import com.m2u.eyelink.rpc.packet.SendPacket;
import com.m2u.eyelink.sender.ServerStreamChannelContext;
import com.m2u.eyelink.sender.StreamClosePacket;
import com.m2u.eyelink.sender.StreamCode;
import com.m2u.eyelink.sender.StreamCreatePacket;

public class ClusterPointRouter implements MessageListener, ServerStreamChannelMessageListener {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ClusterPointRepository<TargetClusterPoint> targetClusterPointRepository;

    private final DefaultRouteHandler routeHandler;
    private final StreamRouteHandler streamRouteHandler;

    @Autowired
    private SerializerFactory<HeaderTBaseSerializer> commandSerializerFactory;

    @Autowired
    private DeserializerFactory<HeaderTBaseDeserializer> commandDeserializerFactory;

    public ClusterPointRouter(ClusterPointRepository<TargetClusterPoint> targetClusterPointRepository,
            DefaultRouteHandler defaultRouteHandler, StreamRouteHandler streamRouteHandler) {
        if (targetClusterPointRepository == null) {
            throw new NullPointerException("targetClusterPointRepository may not be null");
        }
        if (defaultRouteHandler == null) {
            throw new NullPointerException("defaultRouteHandler may not be null");
        }
        if (streamRouteHandler == null) {
            throw new NullPointerException("streamRouteHandler may not be null");
        }
        this.targetClusterPointRepository = targetClusterPointRepository;
        this.routeHandler = defaultRouteHandler;
        this.streamRouteHandler = streamRouteHandler;
    }

    @PreDestroy
    public void stop() {
    }

    @Override
    public void handleSend(SendPacket sendPacket, ELAgentSocket pinpointSocket) {
        logger.info("handleSend packet:{}, remote:{}", sendPacket, pinpointSocket.getRemoteAddress());
    }

    @Override
    public void handleRequest(RequestPacket requestPacket, ELAgentSocket pinpointSocket) {
        logger.info("handleRequest packet:{}, remote:{}", requestPacket, pinpointSocket.getRemoteAddress());

        TBase<?, ?> request = deserialize(requestPacket.getPayload());
        if (request == null) {
            handleRouteRequestFail("Protocol decoding failed.", requestPacket, pinpointSocket);
        } else if (request instanceof TCommandTransfer) {
            handleRouteRequest((TCommandTransfer)request, requestPacket, pinpointSocket);
        } else {
            handleRouteRequestFail("Unknown error.", requestPacket, pinpointSocket);
        }
    }

    @Override
    public StreamCode handleStreamCreate(ServerStreamChannelContext streamChannelContext, StreamCreatePacket packet) {
        logger.info("handleStreamCreate packet:{}, streamChannel:{}", packet, streamChannelContext);

        TBase<?, ?> request = deserialize(packet.getPayload());
        if (request == null) {
            return StreamCode.TYPE_UNKNOWN;
        } else if (request instanceof TCommandTransfer) {
            return handleStreamRouteCreate((TCommandTransfer)request, packet, streamChannelContext);
        } else {
            return StreamCode.TYPE_UNSUPPORT;
        }
    }

    @Override
    public void handleStreamClose(ServerStreamChannelContext streamChannelContext, StreamClosePacket packet) {
        logger.info("handleStreamClose packet:{}, streamChannel:{}", packet, streamChannelContext);

        streamRouteHandler.close(streamChannelContext);
    }

    private boolean handleRouteRequest(TCommandTransfer request, RequestPacket requestPacket, ELAgentSocket pinpointSocket) {
        byte[] payload = ((TCommandTransfer)request).getPayload();
        TBase<?,?> command = deserialize(payload);

        TCommandTransferResponse response = routeHandler.onRoute(new RequestEvent((TCommandTransfer) request, pinpointSocket.getRemoteAddress(), requestPacket.getRequestId(), command));
        pinpointSocket.response(requestPacket, serialize(response));

        return response.getRouteResult() == TRouteResult.OK;
    }

    private void handleRouteRequestFail(String message, RequestPacket requestPacket, ELAgentSocket pinpointSocket) {
        TResult tResult = new TResult(false);
        tResult.setMessage(message);

        pinpointSocket.response(requestPacket, serialize(tResult));
    }

    private StreamCode handleStreamRouteCreate(TCommandTransfer request, StreamCreatePacket packet, ServerStreamChannelContext streamChannelContext) {
        byte[] payload = ((TCommandTransfer)request).getPayload();
        TBase<?,?> command = deserialize(payload);
        if (command == null) {
            return StreamCode.TYPE_UNKNOWN;
        }

        TCommandTransferResponse response = streamRouteHandler.onRoute(new StreamEvent((TCommandTransfer) request, streamChannelContext, command));
        TRouteResult routeResult = response.getRouteResult();
        if (routeResult != TRouteResult.OK) {
            logger.warn("handleStreamRouteCreate failed. command:{}, routeResult:{}", command, routeResult);
            return convertToStreamCode(routeResult);
        }

        return StreamCode.OK;
    }

    public ClusterPointRepository<TargetClusterPoint> getTargetClusterPointRepository() {
        return targetClusterPointRepository;
    }

    private byte[] serialize(TBase<?,?> result) {
        return SerializationUtils.serialize(result, commandSerializerFactory, null);
    }

    private TBase<?,?> deserialize(byte[] objectData) {
        return SerializationUtils.deserialize(objectData, commandDeserializerFactory, null);
    }

    private StreamCode convertToStreamCode(TRouteResult routeResult) {
        switch (routeResult) {
            case NOT_SUPPORTED_REQUEST:
                return StreamCode.TYPE_UNSUPPORT;
            case NOT_ACCEPTABLE:
            case NOT_SUPPORTED_SERVICE:
                return StreamCode.CONNECTION_UNSUPPORT;
            default:
                return StreamCode.ROUTE_ERROR;
        }
    }

}
