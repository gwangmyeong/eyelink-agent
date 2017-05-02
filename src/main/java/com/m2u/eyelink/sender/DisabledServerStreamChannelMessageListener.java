package com.m2u.eyelink.sender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DisabledServerStreamChannelMessageListener implements ServerStreamChannelMessageListener {
    public static final ServerStreamChannelMessageListener INSTANCE = new DisabledServerStreamChannelMessageListener();

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public StreamCode handleStreamCreate(ServerStreamChannelContext streamChannelContext, StreamCreatePacket packet) {
        logger.info("{} handleStreamCreate unsupported operation. StreamChannel:{}, Packet:{}", this.getClass().getSimpleName(), streamChannelContext, packet);
        return StreamCode.CONNECTION_UNSUPPORT;
    }

    @Override
    public void handleStreamClose(ServerStreamChannelContext streamChannelContext, StreamClosePacket packet) {
        logger.info("{} handleStreamClose unsupported operation. StreamChannel:{}, Packet:{}", this.getClass().getSimpleName(), streamChannelContext, packet);
    }

}
