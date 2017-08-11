package com.m2u.eyelink.collector.cluster;

import org.apache.thrift.TBase;

public interface TargetClusterPoint extends ClusterPoint {

    String getApplicationName();

    String getAgentId();

    long getStartTimeStamp();

    String gerVersion();

    boolean isSupportCommand(TBase command);

}
