package com.m2u.eyelink.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingStateChangeEventListener implements StateChangeEventListener<ELAgentSocket> {

    public static final LoggingStateChangeEventListener INSTANCE = new LoggingStateChangeEventListener();

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void eventPerformed(ELAgentSocket elagentSocket, SocketStateCode stateCode) throws Exception {
        logger.info("eventPerformed socket:{}, stateCode:{}", elagentSocket, stateCode);
    }

    @Override
    public void exceptionCaught(ELAgentSocket elagentSocket, SocketStateCode stateCode, Throwable e) {
        logger.warn("exceptionCaught message:{}, socket:{}, stateCode:{}", e.getMessage(), elagentSocket, stateCode, e);
    }

}
