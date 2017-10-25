package com.m2u.eyelink.collector.cluster.connection;

import com.m2u.eyelink.rpc.MessageListener;
import com.m2u.eyelink.rpc.ServerStreamChannelMessageListener;

public interface CollectorClusterConnectionOption {

    String getClusterId();

    MessageListener getRouteMessageHandler();

    ServerStreamChannelMessageListener getRouteStreamMessageHandler();

}
