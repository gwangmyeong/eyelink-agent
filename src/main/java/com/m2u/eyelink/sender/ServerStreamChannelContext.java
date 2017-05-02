package com.m2u.eyelink.sender;

public class ServerStreamChannelContext extends StreamChannelContext{
    private ServerStreamChannel streamChannel;

    public ServerStreamChannelContext(ServerStreamChannel streamChannel) {
        this.streamChannel = streamChannel;
    }

    @Override
    public ServerStreamChannel getStreamChannel() {
        return streamChannel;
    }
}
