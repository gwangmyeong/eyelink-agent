package com.m2u.eyelink.agent.profiler.monitor.collector.datasource;

import com.m2u.eyelink.context.thrift.TDataSourceList;

public class UnsupportedDataSourceMetricCollector implements DataSourceMetricCollector {

    @Override
    public TDataSourceList collect() {
        return null;
    }

    @Override
    public String toString() {
        return "Unsupported DataSourceMetricCollector";
    }
}
