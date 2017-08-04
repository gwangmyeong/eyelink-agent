package com.m2u.eyelink.collector.util;

import java.net.DatagramPacket;

public class DatagramPacketFactory implements ObjectPoolFactory<DatagramPacket> {

    public static final int UDP_MAX_PACKET_LENGTH = 65507;

    private final int bufferLength;

    public DatagramPacketFactory() {
        this(UDP_MAX_PACKET_LENGTH);
    }

    public DatagramPacketFactory(int bufferLength) {
        if (bufferLength < 0 ) {
            throw new IllegalArgumentException("negative bufferLength:" + bufferLength);
        }
        this.bufferLength = bufferLength;
    }

    @Override
    public DatagramPacket create() {
        byte[] bytes = new byte[bufferLength];
        return new DatagramPacket(bytes, 0, bytes.length);
    }

    @Override
    public void beforeReturn(DatagramPacket packet) {
        packet.setLength(bufferLength);
    }
}
