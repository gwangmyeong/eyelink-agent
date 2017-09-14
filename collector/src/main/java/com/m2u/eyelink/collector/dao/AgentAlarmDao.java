package com.m2u.eyelink.collector.dao;

import com.m2u.eyelink.collector.mapper.thrift.stat.AgentStatBo;

public interface AgentAlarmDao {

	void insert(String agentId, AgentStatBo agentStatBo);
	
}
