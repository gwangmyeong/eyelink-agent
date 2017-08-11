package com.m2u.eyelink.collector.cluster;

import com.m2u.eyelink.collector.config.CollectorConfiguration;

public abstract class AbstractClusterService implements ClusterService {

    protected final CollectorConfiguration config;
    protected final ClusterPointRouter clusterPointRouter;

    public AbstractClusterService(CollectorConfiguration config, ClusterPointRouter clusterPointRouter) {
        this.config = config;
        this.clusterPointRouter = clusterPointRouter;
    }

}
