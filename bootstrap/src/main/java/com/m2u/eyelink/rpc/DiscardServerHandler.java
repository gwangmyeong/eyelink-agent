package com.m2u.eyelink.rpc;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiscardServerHandler extends SimpleChannelUpstreamHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private long transferredBytes;

    public long getTransferredBytes() {
        return transferredBytes;
    }

    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (e instanceof ChannelStateEvent) {
            logger.debug("event:{}", e);
        }

    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {

        transferredBytes += ((ChannelBuffer) e.getMessage()).readableBytes();
        logger.debug("messageReceived. meg:{} channel:{}", e.getMessage(), e.getChannel());
        logger.debug("transferredBytes. transferredBytes:{}", transferredBytes);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        logger.warn("Unexpected exception from downstream. Caused:{}", e, e.getCause());
    }
}
