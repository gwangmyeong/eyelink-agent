package com.m2u.eyelink.sender;

public interface ClientStreamChannelMessageListener {
	   void handleStreamData(ClientStreamChannelContext streamChannelContext, StreamResponsePacket packet);

	    void handleStreamClose(ClientStreamChannelContext streamChannelContext, StreamClosePacket packet);

}
