package com.m2u.eyelink.collector.cluster.connection;

import java.net.InetSocketAddress;

import com.m2u.eyelink.rpc.MessageListener;
import com.m2u.eyelink.rpc.ServerStreamChannelMessageListener;

public class CollectorClusterConnectionFactory implements CollectorClusterConnectionOption {

    private final String clusterId;

    private final MessageListener routeMessageHandler;

    private final ServerStreamChannelMessageListener routeStreamMessageHandler;

    public CollectorClusterConnectionFactory(String clusterId, MessageListener routeMessageHandler, ServerStreamChannelMessageListener routeStreamMessageHandler) {
        this.clusterId = clusterId;
        this.routeMessageHandler = routeMessageHandler;
        this.routeStreamMessageHandler = routeStreamMessageHandler;
    }

    public CollectorClusterConnector createConnector() {
        return new CollectorClusterConnector(this);
    }

    public CollectorClusterAcceptor createAcceptor(InetSocketAddress bindAddress, CollectorClusterConnectionRepository clusterSocketRepository) {
        return new CollectorClusterAcceptor(this, bindAddress, clusterSocketRepository);
    }

    @Override
    public String getClusterId() {
        return clusterId;
    }

    @Override
    public MessageListener getRouteMessageHandler() {
        return routeMessageHandler;
    }

    @Override
    public ServerStreamChannelMessageListener getRouteStreamMessageHandler() {
        return routeStreamMessageHandler;
    }

}
