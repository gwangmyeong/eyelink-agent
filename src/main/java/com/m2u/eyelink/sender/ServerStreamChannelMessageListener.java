package com.m2u.eyelink.sender;

public interface ServerStreamChannelMessageListener {

    StreamCode handleStreamCreate(ServerStreamChannelContext streamChannelContext, StreamCreatePacket packet);

    void handleStreamClose(ServerStreamChannelContext streamChannelContext, StreamClosePacket packet);

}