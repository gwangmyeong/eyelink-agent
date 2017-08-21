package com.m2u.eyelink.collector.dao;

import com.m2u.eyelink.context.thrift.TAgentStat;

@Deprecated
public interface AgentStatDao {
    @Deprecated void insert(TAgentStat agentStat);
}
