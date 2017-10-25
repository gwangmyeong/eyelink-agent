package com.m2u.eyelink.collector.cluster;

import java.util.List;

public interface ClusterPointLocator<T extends ClusterPoint> {

    List<T> getClusterPointList();

}
