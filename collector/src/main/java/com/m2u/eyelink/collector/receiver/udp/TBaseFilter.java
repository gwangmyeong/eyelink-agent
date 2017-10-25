package com.m2u.eyelink.collector.receiver.udp;

import java.net.DatagramSocket;
import java.net.SocketAddress;

import org.apache.thrift.TBase;

public interface TBaseFilter<T> {
    boolean CONTINUE = true;
    boolean BREAK = false;

    boolean filter(DatagramSocket localSocket, TBase<?, ?> tBase, T remoteHostAddress);

    // TODO fix generic type
    TBaseFilter CONTINUE_FILTER = new TBaseFilter<SocketAddress>() {

        @Override
        public boolean filter(DatagramSocket localSocket, TBase<?, ?> tBase, SocketAddress remoteHostAddress) {
            return CONTINUE;
        }

    };
}
