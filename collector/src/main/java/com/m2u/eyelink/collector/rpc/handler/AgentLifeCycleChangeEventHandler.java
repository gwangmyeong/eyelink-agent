package com.m2u.eyelink.collector.rpc.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.m2u.eyelink.collector.server.util.AgentLifeCycleState;
import com.m2u.eyelink.collector.util.AgentEventType;
import com.m2u.eyelink.collector.util.ManagedAgentLifeCycle;
import com.m2u.eyelink.rpc.SocketStateCode;
import com.m2u.eyelink.rpc.server.ELAgentServer;
import com.m2u.eyelink.rpc.server.ServerStateChangeEventHandler;

public class AgentLifeCycleChangeEventHandler implements ServerStateChangeEventHandler {

    public static final ManagedAgentLifeCycle STATE_NOT_MANAGED = null;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AgentLifeCycleHandler agentLifeCycleHandler;

    @Autowired
    private AgentEventHandler agentEventHandler;

    @Override
    public void eventPerformed(ELAgentServer pinpointServer, SocketStateCode stateCode) throws Exception {
        ManagedAgentLifeCycle managedAgentLifeCycle = ManagedAgentLifeCycle.getManagedAgentLifeCycleByStateCode(stateCode);
        if (managedAgentLifeCycle == STATE_NOT_MANAGED) {
            return;
        } else {
            logger.info("{} eventPerformed(). pinpointServer:{}, code:{}", this.getClass().getSimpleName(), pinpointServer, stateCode);
            
            long eventTimestamp = System.currentTimeMillis();

            AgentLifeCycleState agentLifeCycleState = managedAgentLifeCycle.getMappedState();
            this.agentLifeCycleHandler.handleLifeCycleEvent(pinpointServer, eventTimestamp, agentLifeCycleState, managedAgentLifeCycle.getEventCounter());

            AgentEventType agentEventType = managedAgentLifeCycle.getMappedEvent();
            this.agentEventHandler.handleEvent(pinpointServer, eventTimestamp, agentEventType);
        }
    }

    @Override
    public void exceptionCaught(ELAgentServer pinpointServer, SocketStateCode stateCode, Throwable e) {
        logger.warn("{} exceptionCaught(). pinpointServer:{}, code:{}. error: {}.",
                this.getClass().getSimpleName(), pinpointServer, stateCode, e.getMessage(), e);
    }

}
