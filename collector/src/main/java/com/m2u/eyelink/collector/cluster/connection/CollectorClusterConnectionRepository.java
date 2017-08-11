package com.m2u.eyelink.collector.cluster.connection;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.m2u.eyelink.rpc.ELAgentSocket;

public class CollectorClusterConnectionRepository {

    private final ConcurrentHashMap<SocketAddress, ELAgentSocket> clusterConnectionRepository = new ConcurrentHashMap<>();

    public ELAgentSocket putIfAbsent(SocketAddress address, ELAgentSocket pinpointSocket) {
        return clusterConnectionRepository.putIfAbsent(address, pinpointSocket);
    }

    public ELAgentSocket remove(SocketAddress address) {
        return clusterConnectionRepository.remove(address);
    }

    public boolean containsKey(SocketAddress address) {
        return clusterConnectionRepository.containsKey(address);
    }

    public List<SocketAddress> getAddressList() {
        // fix jdk 8 KeySetView compatibility
        Set<SocketAddress> socketAddresses = clusterConnectionRepository.keySet();
        return new ArrayList<>(socketAddresses);
    }

    public List<ELAgentSocket> getClusterSocketList() {
        return new ArrayList<>(clusterConnectionRepository.values());
    }

}
