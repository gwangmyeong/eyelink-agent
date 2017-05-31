package com.m2u.eyelink.agent.profiler.context;

import java.util.List;

import com.m2u.eyelink.trace.ServiceType;

public interface DatabaseInfo {

    List<String> getHost();

    String getMultipleHost();

    String getDatabaseId();

    String getRealUrl();

    String getUrl();

    ServiceType getType();

    ServiceType getExecuteQueryType();

    boolean isParsingComplete();

}
