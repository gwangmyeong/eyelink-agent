package com.m2u.eyelink.agent.profiler.monitor.metric.gc;

import java.util.Map;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import com.m2u.eyelink.agent.profiler.monitor.codahale.MetricMonitorValues;
import com.m2u.eyelink.thrift.dto.TJvmGcType;

public class CmsGcGarbageCollectorMetric implements GarbageCollectorMetric {

    private static final TJvmGcType GC_TYPE = TJvmGcType.CMS;

    private final Gauge<Long> gcOldCountGauge;
    private final Gauge<Long> gcOldTimeGauge;

    private final Gauge<Long> gcNewCountGauge;
    private final Gauge<Long> gcNewTimeGauge;

    @SuppressWarnings("unchecked")
    public CmsGcGarbageCollectorMetric(Map<String, Metric> garbageCollectorMetrics) {
        if (garbageCollectorMetrics == null) {
            throw new NullPointerException("garbageCollectorMetrics must not be null");
        }
        gcOldCountGauge = (Gauge<Long>) MetricMonitorValues.getMetric(garbageCollectorMetrics, MetricMonitorValues.METRIC_GC_CMS_OLDGEN_COUNT, EMPTY_LONG_GAUGE);
        gcOldTimeGauge = (Gauge<Long>) MetricMonitorValues.getMetric(garbageCollectorMetrics, MetricMonitorValues.METRIC_GC_CMS_OLDGEN_TIME, EMPTY_LONG_GAUGE);
        gcNewCountGauge = (Gauge<Long>) MetricMonitorValues.getMetric(garbageCollectorMetrics, MetricMonitorValues.METRIC_GC_CMS_NEWGEN_COUNT, EMPTY_LONG_GAUGE);
        gcNewTimeGauge = (Gauge<Long>) MetricMonitorValues.getMetric(garbageCollectorMetrics, MetricMonitorValues.METRIC_GC_CMS_NEWGEN_TIME, EMPTY_LONG_GAUGE);
    }

    @Override
    public TJvmGcType gcType() {
        return GC_TYPE;
    }

    @Override
    public Long gcOldCount() {
        return gcOldCountGauge.getValue();
    }

    @Override
    public Long gcOldTime() {
        return gcOldTimeGauge.getValue();
    }

    @Override
    public Long gcNewCount() {
        return gcNewCountGauge.getValue();
    }

    @Override
    public Long gcNewTime() {
        return gcNewTimeGauge.getValue();
    }

    @Override
    public String toString() {
        return "HotSpot's Concurrent-Mark-Sweep gc garbage metrics";
    }
}
