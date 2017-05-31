package com.m2u.eyelink.agent.profiler.monitor.metric.memory;

import java.util.Map;

import com.codahale.metrics.Metric;

public class UnknownMemoryMetric extends CommonMemoryMetric {

    public UnknownMemoryMetric(Map<String, Metric> memoryUsageMetrics) {
        super(memoryUsageMetrics);
    }

    @Override
    public Double newGenUsage() {
        return EMPTY_DOUBLE_GAUGE.getValue();
    }

    @Override
    public Double oldGenUsage() {
        return EMPTY_DOUBLE_GAUGE.getValue();
    }

    @Override
    public Double codeCacheUsage() {
        return EMPTY_DOUBLE_GAUGE.getValue();
    }

    @Override
    public Double survivorUsage() {
        return EMPTY_DOUBLE_GAUGE.getValue();
    }

    @Override
    public Double permGenUsage() {
        return EMPTY_DOUBLE_GAUGE.getValue();
    }

    @Override
    public Double metaspaceUsage() {
        return EMPTY_DOUBLE_GAUGE.getValue();
    }

    @Override
    public String toString() {
        return "Unknown memory metrics";
    }
}
