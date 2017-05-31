package com.m2u.eyelink.agent.profiler.monitor.metric.memory;

import com.codahale.metrics.Gauge;
import com.m2u.eyelink.agent.profiler.monitor.codahale.MetricMonitorValues;

public interface MemoryMetric {

    Gauge<Long> EMPTY_LONG_GAUGE = new MetricMonitorValues.EmptyGauge<Long>(-1L);
    Gauge<Double> EMPTY_DOUBLE_GAUGE = new MetricMonitorValues.EmptyGauge<Double>(-1D);

    Long heapMax();

    Long heapUsed();

    Long nonHeapMax();

    Long nonHeapUsed();

    Double newGenUsage();

    Double oldGenUsage();

    Double codeCacheUsage();

    Double survivorUsage();

    Double permGenUsage();

    Double metaspaceUsage();
}