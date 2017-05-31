package com.m2u.eyelink.context;

import com.m2u.eyelink.trace.ServiceType;

public interface AgentInformation {
    String getAgentId();

    String getApplicationName();

    long getStartTime();

    int getPid();

    String getMachineName();

    String getHostIp();

    ServiceType getServerType();

    String getJvmVersion();

    String getAgentVersion();
}
