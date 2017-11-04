package com.m2u.eyelink.collector.rpc.handler;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import javax.annotation.Resource;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.collector.bo.AgentEventBo;
import com.m2u.eyelink.collector.cluster.route.ResponseEvent;
import com.m2u.eyelink.collector.dao.AgentEventDao;
import com.m2u.eyelink.collector.server.util.AgentEventMessageSerializer;
import com.m2u.eyelink.collector.util.AgentEventType;
import com.m2u.eyelink.collector.util.AgentEventTypeCategory;
import com.m2u.eyelink.collector.util.MapUtils;
import com.m2u.eyelink.context.HandshakePropertyType;
import com.m2u.eyelink.rpc.server.ELAgentServer;
import com.m2u.eyelink.thrift.DeserializerFactory;
import com.m2u.eyelink.thrift.HeaderTBaseDeserializer;
import com.m2u.eyelink.thrift.SerializationUtils;
import com.m2u.eyelink.thrift.TCommandTransfer;
import com.m2u.eyelink.thrift.TCommandTransferResponse;
import com.m2u.eyelink.thrift.TRouteResult;

public class AgentEventHandler {

    private static final Set<AgentEventType> RESPONSE_EVENT_TYPES = AgentEventType
            .getTypesByCatgory(AgentEventTypeCategory.USER_REQUEST);

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource(name = "agentEventWorker")
    private Executor executor;

    @Resource
    private AgentEventDao agentEventDao;

    @Resource
    private AgentEventMessageSerializer agentEventMessageSerializer;

    @Resource
    private DeserializerFactory<HeaderTBaseDeserializer> commandDeserializerFactory;

    public void handleEvent(ELAgentServer pinpointServer, long eventTimestamp, AgentEventType eventType) {
        handleEvent(pinpointServer, eventTimestamp, eventType, null);
    }

    public void handleEvent(ELAgentServer elagentServer, long eventTimestamp, AgentEventType eventType,
            Object eventMessage) {
        if (elagentServer == null) {
            throw new NullPointerException("ELAgentServer may not be null");
        }
        if (eventType == null) {
            throw new NullPointerException("eventType may not be null");
        }

        Map<Object, Object> channelProperties = elagentServer.getChannelProperties();

        final String agentId = MapUtils.getString(channelProperties, HandshakePropertyType.AGENT_ID.getName());
        final long startTimestamp = MapUtils.getLong(channelProperties,
                HandshakePropertyType.START_TIMESTAMP.getName());

        this.executor.execute(new AgentEventHandlerDispatch(agentId, startTimestamp, eventTimestamp, eventType,
                eventMessage));
    }

    public void handleResponseEvent(ResponseEvent responseEvent, long eventTimestamp) {
        if (responseEvent == null) {
            throw new NullPointerException("responseEvent may not be null");
        }
        TCommandTransferResponse response = responseEvent.getRouteResult();
        if (response.getRouteResult() != TRouteResult.OK) {
            return;
        }
        this.executor.execute(new AgentResponseEventHandlerDispatch(responseEvent, eventTimestamp));
    }

    private class AgentEventHandlerDispatch implements Runnable {
        private final String agentId;
        private final long startTimestamp;
        private final long eventTimestamp;
        private final AgentEventType eventType;
        private final Object eventMessage;

        private AgentEventHandlerDispatch(String agentId, long startTimestamp, long eventTimestamp,
                AgentEventType eventType, Object eventMessage) {
            this.agentId = agentId;
            this.startTimestamp = startTimestamp;
            this.eventTimestamp = eventTimestamp;
            this.eventType = eventType;
            this.eventMessage = eventMessage;
        }

        @Override
        public void run() {
            AgentEventBo event = new AgentEventBo(this.agentId, this.startTimestamp,
                    this.eventTimestamp, this.eventType);
            try {
                byte[] eventBody = agentEventMessageSerializer.serialize(this.eventType, this.eventMessage);
                event.setEventBody(eventBody);
            } catch (Exception e) {
                logger.warn("error handling agent event", e);
                return;
            }
            logger.info("handle event: {}", event);
            agentEventDao.insert(event);
        }

    }

    private class AgentResponseEventHandlerDispatch implements Runnable {
        private final String agentId;
        private final long startTimestamp;
        private final long eventTimestamp;
        private final byte[] payload;

        private AgentResponseEventHandlerDispatch(ResponseEvent responseEvent, long eventTimestamp) {
            final TCommandTransfer command = responseEvent.getDeliveryCommand();
            this.agentId = command.getAgentId();
            this.startTimestamp = command.getStartTime();
            this.eventTimestamp = eventTimestamp;
            final TCommandTransferResponse response = responseEvent.getRouteResult();
            this.payload = response.getPayload();
        }

        @Override
        public void run() {
            Class<?> payloadType = Void.class;
            if (this.payload != null) {
                try {
                    payloadType = SerializationUtils.deserialize(this.payload, commandDeserializerFactory).getClass();
                } catch (TException e) {
                    logger.warn("Error deserializing ResponseEvent payload", e);
                    return;
                }
            }
            for (AgentEventType eventType : RESPONSE_EVENT_TYPES) {
                if (eventType.getMessageType() == payloadType) {
                    AgentEventBo agentEventBo = new AgentEventBo(this.agentId, this.startTimestamp,
                            this.eventTimestamp, eventType);
                    agentEventBo.setEventBody(this.payload);
                    agentEventDao.insert(agentEventBo);
                }
            }
        }
    }

}
