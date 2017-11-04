package com.m2u.eyelink.agent.profiler.monitor.collector.activethread;

import com.m2u.eyelink.agent.profiler.monitor.metric.activethread.ActiveTraceMetric;
import com.m2u.eyelink.thrift.TActiveTrace; 

public class DefaultActiveTraceMetricCollector implements ActiveTraceMetricCollector {

    private final ActiveTraceMetric activeTraceMetric;

    public DefaultActiveTraceMetricCollector(ActiveTraceMetric activeTraceMetric) {
        if (activeTraceMetric == null) {
            throw new NullPointerException("activeTraceMetric must not be null");
        }
        this.activeTraceMetric = activeTraceMetric;
    }

    @Override
    public TActiveTrace collect() {
        TActiveTrace activeTrace = new TActiveTrace();
        activeTrace.setHistogram(activeTraceMetric.activeTraceHistogram());
        return activeTrace;
    }
}
