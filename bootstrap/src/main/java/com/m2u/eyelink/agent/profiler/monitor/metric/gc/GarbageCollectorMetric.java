package com.m2u.eyelink.agent.profiler.monitor.metric.gc;

import com.codahale.metrics.Gauge;
import com.m2u.eyelink.agent.profiler.monitor.codahale.MetricMonitorValues;
import com.m2u.eyelink.thrift.dto.TJvmGcType;

public interface GarbageCollectorMetric {

    Gauge<Long> EMPTY_LONG_GAUGE = new MetricMonitorValues.EmptyGauge<Long>(-1L);

    TJvmGcType gcType();

    Long gcOldCount();

    Long gcOldTime();

    Long gcNewCount();

    Long gcNewTime();
}