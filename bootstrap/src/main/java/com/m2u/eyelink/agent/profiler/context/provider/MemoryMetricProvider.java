package com.m2u.eyelink.agent.profiler.context.provider;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Metric;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.m2u.eyelink.agent.profiler.monitor.codahale.MetricMonitorValues;
import com.m2u.eyelink.agent.profiler.monitor.metric.memory.CmsGcMemoryMetric;
import com.m2u.eyelink.agent.profiler.monitor.metric.memory.G1GcMemoryMetric;
import com.m2u.eyelink.agent.profiler.monitor.metric.memory.MemoryMetric;
import com.m2u.eyelink.agent.profiler.monitor.metric.memory.ParallelGcMemoryMetric;
import com.m2u.eyelink.agent.profiler.monitor.metric.memory.SerialGcMemoryMetric;
import com.m2u.eyelink.agent.profiler.monitor.metric.memory.UnknownMemoryMetric;

public class MemoryMetricProvider implements Provider<MemoryMetric> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MemoryUsageGaugeSet memoryUsageGaugeSet = new MemoryUsageGaugeSet();

    @Inject
    public MemoryMetricProvider() {
    }

    @Override
    public MemoryMetric get() {
        Map<String, Metric> memoryUsageMetrics = memoryUsageGaugeSet.getMetrics();
        Set<String> metricNames = memoryUsageMetrics.keySet();

        MemoryMetric memoryMetric;
        if (metricNames.contains(MetricMonitorValues.METRIC_MEMORY_POOLS_SERIAL_OLDGEN_USAGE)) {
            memoryMetric = new SerialGcMemoryMetric(memoryUsageMetrics);
        } else if (metricNames.contains(MetricMonitorValues.METRIC_MEMORY_POOLS_PS_OLDGEN_USAGE)) {
            memoryMetric = new ParallelGcMemoryMetric(memoryUsageMetrics);
        } else if (metricNames.contains(MetricMonitorValues.METRIC_MEMORY_POOLS_CMS_OLDGEN_USAGE)) {
            memoryMetric = new CmsGcMemoryMetric(memoryUsageMetrics);
        } else if (metricNames.contains(MetricMonitorValues.METRIC_MEMORY_POOLS_G1_OLDGEN_USAGE)) {
            memoryMetric = new G1GcMemoryMetric(memoryUsageMetrics);
        } else {
            memoryMetric = new UnknownMemoryMetric(memoryUsageMetrics);
        }
        logger.info("loaded : {}", memoryMetric);
        return memoryMetric;
    }
}
