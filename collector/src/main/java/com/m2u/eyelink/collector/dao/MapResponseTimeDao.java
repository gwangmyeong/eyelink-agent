package com.m2u.eyelink.collector.dao;

import com.m2u.eyelink.trace.ServiceType;

public interface MapResponseTimeDao extends CachedStatisticsDao {
	void received(String applicationName, ServiceType serviceType, String agentId, int elapsed, boolean isError);
}
