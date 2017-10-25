package com.m2u.eyelink.collector.cluster;

public interface ClusterService {

    void setUp() throws Exception;

    void tearDown() throws Exception;

    boolean isEnable();
}
