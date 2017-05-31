package com.m2u.eyelink.agent.profiler.context.provider.stat.datasource;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.m2u.eyelink.agent.profiler.monitor.collector.datasource.DataSourceMetricCollector;
import com.m2u.eyelink.agent.profiler.monitor.collector.datasource.DefaultDataSourceMetricCollector;
import com.m2u.eyelink.agent.profiler.monitor.collector.datasource.UnsupportedDataSourceMetricCollector;
import com.m2u.eyelink.agent.profiler.monitor.metric.datasource.DataSourceMetric;

public class DataSourceMetricCollectorProvider implements Provider<DataSourceMetricCollector> {

    private final DataSourceMetric dataSourceMetric;

    @Inject
    public DataSourceMetricCollectorProvider(DataSourceMetric dataSourceMetric) {
        if (dataSourceMetric == null) {
            throw new NullPointerException("dataSourceMetric must not be null");
        }
        this.dataSourceMetric = dataSourceMetric;
    }

    @Override
    public DataSourceMetricCollector get() {
        if (dataSourceMetric == DataSourceMetric.UNSUPPORTED_DATA_SOURCE_METRIC) {
            return new UnsupportedDataSourceMetricCollector();
        }
        return new DefaultDataSourceMetricCollector(dataSourceMetric);
    }
}
