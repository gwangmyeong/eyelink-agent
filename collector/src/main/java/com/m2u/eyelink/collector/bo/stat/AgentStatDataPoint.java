package com.m2u.eyelink.collector.bo.stat;

public interface AgentStatDataPoint {
    String getAgentId();
    void setAgentId(String agentId);
    long getStartTimestamp();
    void setStartTimestamp(long startTimestamp);
    long getTimestamp();
    void setTimestamp(long timestamp);
    AgentStatType getAgentStatType();
}
