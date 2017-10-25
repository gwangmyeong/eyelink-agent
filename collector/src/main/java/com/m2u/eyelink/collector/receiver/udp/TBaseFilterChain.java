package com.m2u.eyelink.collector.receiver.udp;

import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.TBase;

public class TBaseFilterChain<T extends SocketAddress> implements TBaseFilter<T> {

    private final List<TBaseFilter<T>> filterChain;

    public TBaseFilterChain(List<TBaseFilter<T>> tBaseFilter) {
        if (tBaseFilter == null) {
            throw new NullPointerException("tBaseFilter must not be null");
        }
        this.filterChain = new ArrayList<>(tBaseFilter);
    }

    @Override
    public boolean filter(DatagramSocket localSocket, TBase<?, ?> tBase, T remoteHostAddress) {
        for (TBaseFilter tBaseFilter : filterChain) {
            if (tBaseFilter.filter(localSocket, tBase, remoteHostAddress) == TBaseFilter.BREAK) {
                return BREAK;
            }
        }
        return TBaseFilter.CONTINUE;
    }

}
