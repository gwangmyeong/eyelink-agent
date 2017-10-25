package com.m2u.eyelink.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingStateChangeEventListener implements StateChangeEventListener<ELAgentSocket> {

    public static final LoggingStateChangeEventListener INSTANCE = new LoggingStateChangeEventListener();

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void eventPerformed(ELAgentSocket pinpointSocket, SocketStateCode stateCode) throws Exception {
        logger.info("eventPerformed socket:{}, stateCode:{}", pinpointSocket, stateCode);
    }

    @Override
    public void exceptionCaught(ELAgentSocket pinpointSocket, SocketStateCode stateCode, Throwable e) {
        logger.warn("exceptionCaught message:{}, socket:{}, stateCode:{}", e.getMessage(), pinpointSocket, stateCode, e);
    }

}
