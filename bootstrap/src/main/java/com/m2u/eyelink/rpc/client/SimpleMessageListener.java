package com.m2u.eyelink.rpc.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.rpc.ELAgentSocket;
import com.m2u.eyelink.rpc.MessageListener;
import com.m2u.eyelink.rpc.RequestPacket;
import com.m2u.eyelink.rpc.packet.SendPacket;

public class SimpleMessageListener implements MessageListener {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final SimpleMessageListener INSTANCE = new SimpleMessageListener();
    public static final SimpleMessageListener ECHO_INSTANCE = new SimpleMessageListener(true);

    private final boolean echo;

    public SimpleMessageListener() {
        this(false);
    }

    public SimpleMessageListener(boolean echo) {
        this.echo = echo;
    }

    @Override
    public void handleSend(SendPacket sendPacket, ELAgentSocket elagentSocket) {
        logger.info("handleSend packet:{}, remote:{}", sendPacket, elagentSocket.getRemoteAddress());
    }

    @Override
    public void handleRequest(RequestPacket requestPacket, ELAgentSocket elagentSocket) {
        logger.info("handleRequest packet:{}, remote:{}", requestPacket, elagentSocket.getRemoteAddress());

        if (echo) {
            elagentSocket.response(requestPacket, requestPacket.getPayload());
        } else {
            elagentSocket.response(requestPacket, new byte[0]);
        }
    }

}
