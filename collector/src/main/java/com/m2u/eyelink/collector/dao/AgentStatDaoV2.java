package com.m2u.eyelink.collector.dao;

import java.util.List;

import com.m2u.eyelink.collector.bo.stat.AgentStatDataPoint;

public interface AgentStatDaoV2<T extends AgentStatDataPoint> {
    void insert(String agentId, List<T> agentStatDataPoints);
}
