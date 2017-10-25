package com.m2u.eyelink.agent.profiler.monitor.metric.datasource;

import com.m2u.eyelink.context.thrift.TDataSourceList;

public interface DataSourceMetric {

    DataSourceMetric UNSUPPORTED_DATA_SOURCE_METRIC = new DataSourceMetric() {
        @Override
        public TDataSourceList dataSourceList() {
            return null;
        }

        @Override
        public String toString() {
            return "Unsupported DataSourceMetric";
        }
    };

    TDataSourceList dataSourceList();
}
