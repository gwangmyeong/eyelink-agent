package com.m2u.eyelink.sender;

import org.jboss.netty.buffer.ChannelBuffer;

public interface Packet {
    short getPacketType();

    byte[] getPayload();

   ChannelBuffer toBuffer();
}
