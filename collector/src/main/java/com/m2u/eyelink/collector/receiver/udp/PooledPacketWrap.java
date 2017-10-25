package com.m2u.eyelink.collector.receiver.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import com.m2u.eyelink.collector.util.PooledObject;

public class PooledPacketWrap implements Runnable {
    private final DatagramSocket localSocket;
    private final PacketHandler<DatagramPacket> packetHandler;
    private final PooledObject<DatagramPacket> pooledObject;

    public PooledPacketWrap(DatagramSocket localSocket, PacketHandler<DatagramPacket> packetHandler, PooledObject<DatagramPacket> pooledObject) {
        if (localSocket == null) {
            throw new NullPointerException("localSocket may not be null");
        }
        if (packetHandler == null) {
            throw new NullPointerException("packetReceiveHandler must not be null");
        }
        if (pooledObject == null) {
            throw new NullPointerException("pooledObject must not be null");
        }
        this.localSocket = localSocket;
        this.packetHandler = packetHandler;
        this.pooledObject = pooledObject;
    }

    @Override
    public void run() {
        final DatagramPacket packet = pooledObject.getObject();
        try {
            packetHandler.receive(localSocket, packet);
        } finally {
            pooledObject.returnObject();
        }
    }
}
