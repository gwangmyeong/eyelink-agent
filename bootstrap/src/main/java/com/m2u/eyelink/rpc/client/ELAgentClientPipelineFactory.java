package com.m2u.eyelink.rpc.client;

import java.util.concurrent.TimeUnit;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.timeout.WriteTimeoutHandler;

import com.m2u.eyelink.rpc.codec.PacketEncoder;

public class ELAgentClientPipelineFactory implements ChannelPipelineFactory {

    private final DefaultELAgentClientFactory elagentClientFactory;

    public ELAgentClientPipelineFactory(DefaultELAgentClientFactory elagentClientFactory) {
        if (elagentClientFactory == null) {
            throw new NullPointerException("elagentClientFactory must not be null");
        }
        this.elagentClientFactory = elagentClientFactory;
    }


    @Override
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = Channels.pipeline();
        pipeline.addLast("encoder", new PacketEncoder());
        pipeline.addLast("decoder", new PacketDecoder());
        
        long pingDelay = elagentClientFactory.getPingDelay();
        long enableWorkerPacketDelay = elagentClientFactory.getEnableWorkerPacketDelay();
        long timeoutMillis = elagentClientFactory.getTimeoutMillis();
        
        DefaultELAgentClientHandler defaultELAgentClientHandler = new DefaultELAgentClientHandler(elagentClientFactory, pingDelay, enableWorkerPacketDelay, timeoutMillis);
        pipeline.addLast("writeTimeout", new WriteTimeoutHandler(defaultELAgentClientHandler.getChannelTimer(), 3000, TimeUnit.MILLISECONDS));
        pipeline.addLast("socketHandler", defaultELAgentClientHandler);
        
        return pipeline;
    }
}
