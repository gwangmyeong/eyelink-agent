package com.m2u.eyelink.collector.cluster;

import com.m2u.eyelink.rpc.Future;

public interface ClusterPoint {

    void send(byte[] data);

    Future request(byte[] data);

}
