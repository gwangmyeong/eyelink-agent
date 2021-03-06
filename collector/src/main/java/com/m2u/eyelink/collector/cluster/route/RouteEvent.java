package com.m2u.eyelink.collector.cluster.route;

import java.net.SocketAddress;

import com.m2u.eyelink.thrift.TCommandTransfer; 

public interface RouteEvent {

    TCommandTransfer getDeliveryCommand();

    SocketAddress getRemoteAddress();

}
