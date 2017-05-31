package com.m2u.eyelink.rpc.client;

import java.util.concurrent.TimeUnit;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.timeout.WriteTimeoutHandler;

import com.m2u.eyelink.rpc.codec.PacketEncoder;

public class ELAgentClientPipelineFactory implements ChannelPipelineFactory {

    private final DefaultELAgentClientFactory pinpointClientFactory;

    public ELAgentClientPipelineFactory(DefaultELAgentClientFactory pinpointClientFactory) {
        if (pinpointClientFactory == null) {
            throw new NullPointerException("pinpointClientFactory must not be null");
        }
        this.pinpointClientFactory = pinpointClientFactory;
    }


    @Override
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = Channels.pipeline();
        pipeline.addLast("encoder", new PacketEncoder());
        pipeline.addLast("decoder", new PacketDecoder());
        
        long pingDelay = pinpointClientFactory.getPingDelay();
        long enableWorkerPacketDelay = pinpointClientFactory.getEnableWorkerPacketDelay();
        long timeoutMillis = pinpointClientFactory.getTimeoutMillis();
        
        DefaultELAgentClientHandler defaultELAgentClientHandler = new DefaultELAgentClientHandler(pinpointClientFactory, pingDelay, enableWorkerPacketDelay, timeoutMillis);
        pipeline.addLast("writeTimeout", new WriteTimeoutHandler(defaultELAgentClientHandler.getChannelTimer(), 3000, TimeUnit.MILLISECONDS));
        pipeline.addLast("socketHandler", defaultELAgentClientHandler);
        
        return pipeline;
    }
}
