package com.m2u.eyelink.collector.dao;

import com.m2u.eyelink.trace.ServiceType;

public interface MapStatisticsCallerDao extends CachedStatisticsDao {
	void update(String callerApplicationName, ServiceType callerServiceType, String callerAgentId,
			String calleeApplicationName, ServiceType calleeServiceType, String calleeHost, int elapsed,
			boolean isError);

	void insert(String transactionId, String callerApplicationName, ServiceType callerServiceType, String callerAgentId,
			String calleeApplicationName, ServiceType calleeServiceType, String calleeHost, long startTime, int elapsed,
			boolean isError);
}
