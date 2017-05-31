package com.m2u.eyelink.agent.profiler.context.provider.stat.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.m2u.eyelink.agent.profiler.context.monitor.DataSourceMonitorRegistryService;
import com.m2u.eyelink.agent.profiler.context.monitor.JdbcUrlParsingService;
import com.m2u.eyelink.agent.profiler.monitor.metric.datasource.DataSourceMetric;
import com.m2u.eyelink.agent.profiler.monitor.metric.datasource.DefaultDataSourceMetric;

public class DataSourceMetricProvider implements Provider<DataSourceMetric> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DataSourceMonitorRegistryService dataSourceMonitorRegistryService;
    private final JdbcUrlParsingService jdbcUrlParsingService;

    @Inject
    public DataSourceMetricProvider(DataSourceMonitorRegistryService dataSourceMonitorRegistryService, JdbcUrlParsingService jdbcUrlParsingService) {
        this.dataSourceMonitorRegistryService = dataSourceMonitorRegistryService;
        this.jdbcUrlParsingService = jdbcUrlParsingService;
    }

    @Override
    public DataSourceMetric get() {
        DataSourceMetric dataSourceMetric;
        if (dataSourceMonitorRegistryService == null || jdbcUrlParsingService == null) {
            dataSourceMetric = DataSourceMetric.UNSUPPORTED_DATA_SOURCE_METRIC;
        } else {
            dataSourceMetric = new DefaultDataSourceMetric(dataSourceMonitorRegistryService, jdbcUrlParsingService);
        }
        logger.info("loaded : {}", dataSourceMetric);
        return dataSourceMetric;
    }
}