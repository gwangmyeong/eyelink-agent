package com.m2u.eyelink.collector.dao;

import com.m2u.eyelink.common.trace.ServiceType;

public interface MapStatisticsCalleeDao extends CachedStatisticsDao {
	void update(String calleeApplicationName, ServiceType calleeServiceType, String callerApplicationName,
			ServiceType callerServiceType, String callerHost, int elapsed, boolean isError);
}
