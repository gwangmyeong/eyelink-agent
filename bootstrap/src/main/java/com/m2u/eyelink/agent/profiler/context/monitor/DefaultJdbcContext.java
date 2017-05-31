package com.m2u.eyelink.agent.profiler.context.monitor;

import com.google.inject.Inject;
import com.m2u.eyelink.agent.profiler.context.DatabaseInfo;
import com.m2u.eyelink.agent.profiler.metadata.JdbcContext;
import com.m2u.eyelink.trace.ServiceType;

public class DefaultJdbcContext implements JdbcContext {

    private final JdbcUrlParsingService jdbcUrlParsingService;

    @Inject
    public DefaultJdbcContext(JdbcUrlParsingService jdbcUrlParsingService) {
        if (jdbcUrlParsingService == null) {
            throw new NullPointerException("jdbcUrlParsingService must not be null");
        }
        this.jdbcUrlParsingService = jdbcUrlParsingService;
    }


    @Override
    public DatabaseInfo parseJdbcUrl(ServiceType serviceType, String jdbcUrl) {
        return this.jdbcUrlParsingService.parseJdbcUrl(serviceType, jdbcUrl);
    }


}