package com.m2u.eyelink.collector.dao;

import com.m2u.eyelink.thrift.TAgentStat;

public interface AgentStatDao {
    @Deprecated void insert(TAgentStat agentStat);
}
