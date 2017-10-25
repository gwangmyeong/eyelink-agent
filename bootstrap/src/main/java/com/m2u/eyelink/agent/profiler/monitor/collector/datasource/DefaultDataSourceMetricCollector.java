package com.m2u.eyelink.agent.profiler.monitor.collector.datasource;

import com.m2u.eyelink.agent.profiler.monitor.metric.datasource.DataSourceMetric;
import com.m2u.eyelink.context.thrift.TDataSourceList;

public class DefaultDataSourceMetricCollector implements DataSourceMetricCollector {

    private final DataSourceMetric dataSourceMetric;

    public DefaultDataSourceMetricCollector(DataSourceMetric dataSourceMetric) {
        if (dataSourceMetric == null) {
            throw new NullPointerException("dataSourceMetric must not be null");
        }
        this.dataSourceMetric = dataSourceMetric;
    }

    @Override
    public TDataSourceList collect() {
        return dataSourceMetric.dataSourceList();
    }
}
