package com.m2u.eyelink.agent.plugin.jdbc;

import com.m2u.eyelink.agent.profiler.context.DatabaseInfo;
import com.m2u.eyelink.common.trace.ServiceType;

public interface JdbcUrlParserV2 {
    DatabaseInfo parse(String url);

    ServiceType getServiceType();
}
