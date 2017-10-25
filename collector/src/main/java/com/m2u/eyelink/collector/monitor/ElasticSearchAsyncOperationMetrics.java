package com.m2u.eyelink.collector.monitor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;
import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchAsyncOperation;

public class ElasticSearchAsyncOperationMetrics implements MetricSet {

    private static final String HBASE_ASYNC_OPS = "hbase.async.ops";
    private static final String COUNT = HBASE_ASYNC_OPS + ".count";
    private static final String REJECTED_COUNT = HBASE_ASYNC_OPS + ".rejected.count";
    private static final String FAILED_COUNT = HBASE_ASYNC_OPS + ".failed.count";
    private static final String WAITING_COUNT = HBASE_ASYNC_OPS + ".waiting.count";
    private static final String AVERAGE_LATENCY = HBASE_ASYNC_OPS + ".latency.value";

    private final ElasticSearchAsyncOperation elasticSearchAsyncOperation;

    public ElasticSearchAsyncOperationMetrics(ElasticSearchAsyncOperation elasticSearchAsyncOperation) {
        if (elasticSearchAsyncOperation == null) {
            throw new NullPointerException("null");
        }
        this.elasticSearchAsyncOperation = elasticSearchAsyncOperation;
    }

    @Override
    public Map<String, Metric> getMetrics() {
        if (!elasticSearchAsyncOperation.isAvailable()) {
            return Collections.emptyMap();
        }

        final Map<String, Metric> gauges = new HashMap<>(3);
        gauges.put(COUNT, new Gauge<Long>() {
            @Override
            public Long getValue() {
                return elasticSearchAsyncOperation.getOpsCount();
            }
        });
        gauges.put(REJECTED_COUNT, new Gauge<Long>() {
            @Override
            public Long getValue() {
                return elasticSearchAsyncOperation.getOpsRejectedCount();
            }
        });
        gauges.put(FAILED_COUNT, new Gauge<Long>() {
            @Override
            public Long getValue() {
                return elasticSearchAsyncOperation.getOpsFailedCount();
            }
        });
        gauges.put(WAITING_COUNT, new Gauge<Long>() {
            @Override
            public Long getValue() {
                return elasticSearchAsyncOperation.getCurrentOpsCount();
            }
        });
        gauges.put(AVERAGE_LATENCY, new Gauge<Long>() {
            @Override
            public Long getValue() {
                return elasticSearchAsyncOperation.getOpsAverageLatency();
            }
        });

        return Collections.unmodifiableMap(gauges);
    }

}
