package com.m2u.eyelink.collector.dao;

import com.m2u.eyelink.trace.ServiceType;

public interface MapStatisticsCallerDao extends CachedStatisticsDao {
	void update(String callerApplicationName, ServiceType callerServiceType, String callerAgentId,
			String calleeApplicationName, ServiceType calleeServiceType, String calleeHost, int elapsed,
			boolean isError);
}
