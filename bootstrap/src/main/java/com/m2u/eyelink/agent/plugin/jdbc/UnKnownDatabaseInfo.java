package com.m2u.eyelink.agent.plugin.jdbc;

import java.util.ArrayList;
import java.util.List;

import com.m2u.eyelink.agent.profiler.context.DatabaseInfo;
import com.m2u.eyelink.common.trace.ServiceType;

public class UnKnownDatabaseInfo {
    public static final DatabaseInfo INSTANCE;

    static{
        final List<String> urls = new ArrayList<String>();
        urls.add("unknown");
        INSTANCE = new DefaultDatabaseInfo(ServiceType.UNKNOWN_DB, ServiceType.UNKNOWN_DB_EXECUTE_QUERY, "unknown", "unknown", urls, "unknown", false);
    }
    
    public static DatabaseInfo createUnknownDataBase(String url) {
        return createUnknownDataBase(ServiceType.UNKNOWN_DB, ServiceType.UNKNOWN_DB_EXECUTE_QUERY, url);
    }

    public static DatabaseInfo createUnknownDataBase(ServiceType type, ServiceType executeQueryType, String url) {
        List<String> list = new ArrayList<String>();
        list.add("error");
        return new DefaultDatabaseInfo(type, executeQueryType, url, url, list, "error", false);
    }

}
