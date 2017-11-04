package com.m2u.eyelink.collector.receiver.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

import org.apache.thrift.TBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

import com.m2u.eyelink.thrift.NetworkAvailabilityCheckPacket; class NetworkAvailabilityCheckPacketFilter<T extends SocketAddress> implements TBaseFilter<T>, DisposableBean {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public NetworkAvailabilityCheckPacketFilter() {
    }

    @Override
    public boolean filter(DatagramSocket localSocket, TBase<?, ?> tBase, T remoteHostAddress) {
        // Network port availability check packet
        if (tBase instanceof NetworkAvailabilityCheckPacket) {
            if (logger.isInfoEnabled()) {
                logger.info("received udp network availability check packet. remoteAddress:{}", remoteHostAddress);
            }
            responseOK(localSocket, remoteHostAddress);
            return BREAK;
        }
        return CONTINUE;
    }

    private void responseOK(DatagramSocket socket, T remoteHostAddress) {
        try {
            byte[] okBytes = NetworkAvailabilityCheckPacket.DATA_OK;
            DatagramPacket pongPacket = new DatagramPacket(okBytes, okBytes.length, remoteHostAddress);
            socket.send(pongPacket);
        } catch (IOException e) {
            if (logger.isWarnEnabled()) {
                logger.warn("pong error. SendSocketAddress:{} Cause:{}", remoteHostAddress, e.getMessage(), e);
            }
        }
    }


    @Override
    public void destroy() throws Exception {
    }

}
