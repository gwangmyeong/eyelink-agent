package com.m2u.eyelink.agent.profiler.monitor.metric.gc;

import com.m2u.eyelink.context.thrift.TJvmGcType;

public class UnknownGarbageCollectorMetric implements GarbageCollectorMetric {

    private static final TJvmGcType GC_TYPE = TJvmGcType.UNKNOWN;

    @Override
    public TJvmGcType gcType() {
        return GC_TYPE;
    }

    @Override
    public Long gcOldCount() {
        return EMPTY_LONG_GAUGE.getValue();
    }

    @Override
    public Long gcOldTime() {
        return EMPTY_LONG_GAUGE.getValue();
    }

    @Override
    public Long gcNewCount() {
        return EMPTY_LONG_GAUGE.getValue();
    }

    @Override
    public Long gcNewTime() {
        return EMPTY_LONG_GAUGE.getValue();
    }

    @Override
    public String toString() {
        return "Unknown garbage collector metrics";
    }
}
