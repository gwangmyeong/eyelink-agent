package com.m2u.eyelink.agent.profiler.context.monitor;

import com.m2u.eyelink.agent.profiler.context.DatabaseInfo;
import com.m2u.eyelink.common.trace.ServiceType;

public interface JdbcUrlParsingService {

    DatabaseInfo getDatabaseInfo(String jdbcUrl);

    DatabaseInfo getDatabaseInfo(ServiceType serviceType, String jdbcUrl);

    DatabaseInfo parseJdbcUrl(ServiceType serviceType, String jdbcUrl);
}
