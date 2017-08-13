package com.m2u.eyelink.collector.dao;

import com.m2u.eyelink.context.thrift.TAgentInfo;

public interface ApplicationIndexDao {
	void insert(final TAgentInfo agentInfo);
}
