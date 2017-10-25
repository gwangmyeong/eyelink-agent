package com.m2u.eyelink.agent.profiler.monitor.collector.activethread;

import com.m2u.eyelink.context.thrift.TActiveTrace;

public class UnsupportedActiveTraceMetricCollector implements ActiveTraceMetricCollector {

    @Override
    public TActiveTrace collect() {
        return null;
    }

    @Override
    public String toString() {
        return "Unsupported CpuLoadMetricCollector";
    }
}
