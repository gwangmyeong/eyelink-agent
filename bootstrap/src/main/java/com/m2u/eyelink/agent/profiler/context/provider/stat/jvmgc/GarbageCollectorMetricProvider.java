package com.m2u.eyelink.agent.profiler.context.provider.stat.jvmgc;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Metric;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.m2u.eyelink.agent.profiler.monitor.codahale.MetricMonitorValues;
import com.m2u.eyelink.agent.profiler.monitor.metric.gc.CmsGcGarbageCollectorMetric;
import com.m2u.eyelink.agent.profiler.monitor.metric.gc.G1GcGarbageCollectorMetric;
import com.m2u.eyelink.agent.profiler.monitor.metric.gc.GarbageCollectorMetric;
import com.m2u.eyelink.agent.profiler.monitor.metric.gc.ParallelGcGarbageCollectorMetric;
import com.m2u.eyelink.agent.profiler.monitor.metric.gc.SerialGcGarbageCollectorMetric;
import com.m2u.eyelink.agent.profiler.monitor.metric.gc.UnknownGarbageCollectorMetric;

public class GarbageCollectorMetricProvider implements Provider<GarbageCollectorMetric> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final GarbageCollectorMetricSet garbageCollectorMetricSet = new GarbageCollectorMetricSet();

    @Inject
    public GarbageCollectorMetricProvider() {
    }

    @Override
    public GarbageCollectorMetric get() {
        Map<String, Metric> garbageCollectorMetrics = garbageCollectorMetricSet.getMetrics();
        Set<String> metricNames = garbageCollectorMetrics.keySet();

        GarbageCollectorMetric garbageCollectorMetric;
        if (metricNames.contains(MetricMonitorValues.METRIC_GC_SERIAL_OLDGEN_COUNT)) {
            garbageCollectorMetric = new SerialGcGarbageCollectorMetric(garbageCollectorMetrics);
        } else if (metricNames.contains(MetricMonitorValues.METRIC_GC_PS_OLDGEN_COUNT)) {
            garbageCollectorMetric = new ParallelGcGarbageCollectorMetric(garbageCollectorMetrics);
        } else if (metricNames.contains(MetricMonitorValues.METRIC_GC_CMS_OLDGEN_COUNT)) {
            garbageCollectorMetric = new CmsGcGarbageCollectorMetric(garbageCollectorMetrics);
        } else if (metricNames.contains(MetricMonitorValues.METRIC_GC_G1_OLDGEN_COUNT)) {
            garbageCollectorMetric = new G1GcGarbageCollectorMetric(garbageCollectorMetrics);
        } else {
            garbageCollectorMetric = new UnknownGarbageCollectorMetric();
        }
        logger.info("loaded : {}", garbageCollectorMetric);
        return garbageCollectorMetric;
    }
}
