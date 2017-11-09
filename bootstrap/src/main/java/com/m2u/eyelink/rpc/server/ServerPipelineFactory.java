package com.m2u.eyelink.rpc.server;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;

import com.m2u.eyelink.rpc.client.PacketDecoder;
import com.m2u.eyelink.rpc.codec.PacketEncoder;
import com.m2u.eyelink.rpc.server.ELAgentServerAcceptor.ELAgentServerChannelHandler;

public class ServerPipelineFactory implements ChannelPipelineFactory {
    private ELAgentServerChannelHandler elagentServerChannelHandler;

    public ServerPipelineFactory(ELAgentServerChannelHandler elagentServerChannelHandler) {
        if (elagentServerChannelHandler == null) {
            throw new NullPointerException("elagentServerFactory");
        }
        this.elagentServerChannelHandler = elagentServerChannelHandler;
    }

    @Override
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = Channels.pipeline();

        pipeline.addLast("decoder", new PacketDecoder());
        pipeline.addLast("encoder", new PacketEncoder());
        pipeline.addLast("handler", elagentServerChannelHandler);

        return pipeline;
    }
}
