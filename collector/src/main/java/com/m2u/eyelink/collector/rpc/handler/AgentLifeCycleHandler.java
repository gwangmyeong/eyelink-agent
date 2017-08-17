package com.m2u.eyelink.collector.rpc.handler;

import java.util.Map;
import java.util.concurrent.Executor;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.m2u.eyelink.collector.bo.AgentLifeCycleBo;
import com.m2u.eyelink.collector.dao.AgentLifeCycleDao;
import com.m2u.eyelink.collector.server.util.AgentLifeCycleState;
import com.m2u.eyelink.context.HandshakePropertyType;
import com.m2u.eyelink.rpc.server.ELAgentServer;
import com.m2u.eyelink.rpc.util.MapUtils;
import com.m2u.eyelink.util.BytesUtils;

public class AgentLifeCycleHandler {

    public static final String SOCKET_ID_KEY = "socketId";

    private static final int INTEGER_BIT_COUNT = BytesUtils.INT_BYTE_LENGTH * 8;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource(name = "agentEventWorker")
    private Executor executor;

    @Autowired
    private AgentLifeCycleDao agentLifeCycleDao;

    public void handleLifeCycleEvent(ELAgentServer elagentServer, long eventTimestamp,
            AgentLifeCycleState agentLifeCycleState, int eventCounter) {
        if (elagentServer == null) {
            throw new NullPointerException("elagentServer may not be null");
        }
        if (agentLifeCycleState == null) {
            throw new NullPointerException("agentLifeCycleState may not be null");
        }
        if (eventCounter < 0) {
            throw new IllegalArgumentException("eventCounter may not be negative");
        }
        logger.info("handle lifecycle event - elagentServer:{}, state:{}", elagentServer, agentLifeCycleState);

        Map<Object, Object> channelProperties = elagentServer.getChannelProperties();
        final Integer socketId = MapUtils.getInteger(channelProperties, SOCKET_ID_KEY);
        if (socketId == null) {
            logger.debug("socketId not found, agent does not support life cycle management - elagentServer:{}",
                    elagentServer);
            return;
        }

        final String agentId = MapUtils.getString(channelProperties, HandshakePropertyType.AGENT_ID.getName());
        final long startTimestamp = MapUtils.getLong(channelProperties, HandshakePropertyType.START_TIMESTAMP.getName());
        final long eventIdentifier = createEventIdentifier(socketId, eventCounter);

        final AgentLifeCycleBo agentLifeCycleBo = new AgentLifeCycleBo(agentId, startTimestamp, eventTimestamp,
                eventIdentifier, agentLifeCycleState);

        this.executor.execute(new AgentLifeCycleHandlerDispatch(agentLifeCycleBo));

    }

    long createEventIdentifier(int socketId, int eventCounter) {
        if (socketId < 0) {
            throw new IllegalArgumentException("socketId may not be less than 0");
        }
        if (eventCounter < 0) {
            throw new IllegalArgumentException("eventCounter may not be less than 0");
        }
        return ((long)socketId << INTEGER_BIT_COUNT) | eventCounter;
    }

    class AgentLifeCycleHandlerDispatch implements Runnable {
        private final AgentLifeCycleBo agentLifeCycleBo;

        private AgentLifeCycleHandlerDispatch(AgentLifeCycleBo agentLifeCycleBo) {
            if (agentLifeCycleBo == null) {
                throw new NullPointerException("agentLifeCycleBo may not be null");
            }
            this.agentLifeCycleBo = agentLifeCycleBo;
        }

        @Override
        public void run() {
            agentLifeCycleDao.insert(this.agentLifeCycleBo);
        }

    }

}
