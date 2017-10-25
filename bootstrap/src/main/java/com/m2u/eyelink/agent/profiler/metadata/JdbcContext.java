package com.m2u.eyelink.agent.profiler.metadata;

import com.m2u.eyelink.agent.profiler.context.DatabaseInfo;
import com.m2u.eyelink.common.trace.ServiceType;

public interface JdbcContext {
	   DatabaseInfo parseJdbcUrl(ServiceType serviceType, String jdbcUrl);

}
