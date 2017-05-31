package com.m2u.eyelink.agent.profiler.plugin.jdbc;

import com.m2u.eyelink.agent.profiler.context.DatabaseInfo;
import com.m2u.eyelink.trace.ServiceType;

public interface JdbcUrlParserV2 {
    DatabaseInfo parse(String url);

    ServiceType getServiceType();
}
