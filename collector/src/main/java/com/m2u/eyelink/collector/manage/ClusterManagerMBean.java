package com.m2u.eyelink.collector.manage;

import java.util.List;

public interface ClusterManagerMBean {

    boolean isEnable();

    List<String> getConnectedAgentList();

}
