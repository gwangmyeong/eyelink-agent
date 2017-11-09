package com.m2u.eyelink.rpc.server.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.rpc.SocketStateCode;
import com.m2u.eyelink.rpc.server.ELAgentServer;
import com.m2u.eyelink.rpc.server.ServerStateChangeEventHandler;

public class DoNothingChannelStateEventHandler implements ServerStateChangeEventHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final ServerStateChangeEventHandler INSTANCE = new DoNothingChannelStateEventHandler();

    @Override
    public void eventPerformed(ELAgentServer elagentServer, SocketStateCode stateCode) {
        logger.info("{} eventPerformed(). elagentServer:{}, code:{}", this.getClass().getSimpleName(), elagentServer, stateCode);
    }
    
    @Override
    public void exceptionCaught(ELAgentServer elagentServer, SocketStateCode stateCode, Throwable e) {
        logger.warn("{} exceptionCaught(). elagentServer:{}, code:{}. Error: {}.", this.getClass().getSimpleName(), elagentServer, stateCode, e.getMessage(), e);
    }

}
