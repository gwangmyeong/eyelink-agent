package com.m2u.eyelink.agent.profiler.context.provider.stat.activethread;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.m2u.eyelink.agent.profiler.monitor.collector.activethread.ActiveTraceMetricCollector;
import com.m2u.eyelink.agent.profiler.monitor.collector.activethread.DefaultActiveTraceMetricCollector;
import com.m2u.eyelink.agent.profiler.monitor.collector.activethread.UnsupportedActiveTraceMetricCollector;
import com.m2u.eyelink.agent.profiler.monitor.metric.activethread.ActiveTraceMetric;

public class ActiveTraceMetricCollectorProvider implements Provider<ActiveTraceMetricCollector> {

    private final ActiveTraceMetric activeTraceMetric;

    @Inject
    public ActiveTraceMetricCollectorProvider(ActiveTraceMetric activeTraceMetric) {
        if (activeTraceMetric == null) {
            throw new NullPointerException("activeTraceMetric must not be null");
        }
        this.activeTraceMetric = activeTraceMetric;
    }

    @Override
    public ActiveTraceMetricCollector get() {
        if (activeTraceMetric == ActiveTraceMetric.UNSUPPORTED_ACTIVE_TRACE_METRIC) {
            return new UnsupportedActiveTraceMetricCollector();
        }
        return new DefaultActiveTraceMetricCollector(activeTraceMetric);
    }
}
