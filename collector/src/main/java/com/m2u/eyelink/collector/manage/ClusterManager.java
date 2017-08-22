package com.m2u.eyelink.collector.manage;

import java.util.ArrayList;
import java.util.List;

import com.m2u.eyelink.collector.cluster.ClusterPointLocator;
import com.m2u.eyelink.collector.cluster.TargetClusterPoint;
import com.m2u.eyelink.collector.config.CollectorConfiguration;

public class ClusterManager extends AbstractCollectorManager implements ClusterManagerMBean {

    private final boolean enableCluster;
    private final ClusterPointLocator clusterPointLocator;

    public ClusterManager(CollectorConfiguration configuration, ClusterPointLocator clusterPointLocator) {
        this.enableCluster = configuration.isClusterEnable();
        this.clusterPointLocator = clusterPointLocator;
    }

    @Override
    public boolean isEnable() {
        return enableCluster;
    }

    @Override
    public List<String> getConnectedAgentList() {
        List<String> result = new ArrayList<>();

        List clusterPointList = clusterPointLocator.getClusterPointList();
        for (Object clusterPoint : clusterPointList) {
            if (clusterPoint instanceof TargetClusterPoint) {
                TargetClusterPoint agentClusterPoint = (TargetClusterPoint) clusterPoint;
                result.add(createAgentKey(agentClusterPoint));
            }
        }

        return result;
    }

    private String createAgentKey(TargetClusterPoint agentClusterPoint) {
        StringBuilder key = new StringBuilder();
        key.append(agentClusterPoint.getApplicationName());
        key.append("/");
        key.append(agentClusterPoint.getAgentId());
        key.append("/");
        key.append(agentClusterPoint.getStartTimeStamp());

        return key.toString();
    }

}
