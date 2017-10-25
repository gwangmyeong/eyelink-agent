package com.m2u.eyelink.agent.profiler.context.monitor;

import com.m2u.eyelink.agent.plugin.jdbc.UnKnownDatabaseInfo;
import com.m2u.eyelink.agent.profiler.context.DatabaseInfo;
import com.m2u.eyelink.agent.profiler.metadata.JdbcContext;
import com.m2u.eyelink.common.trace.ServiceType;

public final class DisabledJdbcContext implements JdbcContext {

    public static final DisabledJdbcContext INSTANCE = new DisabledJdbcContext();

    @Override
    public DatabaseInfo parseJdbcUrl(ServiceType serviceType, String jdbcUrl) {
        return UnKnownDatabaseInfo.createUnknownDataBase(jdbcUrl);
    }

}