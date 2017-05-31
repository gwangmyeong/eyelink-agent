package com.m2u.eyelink.agent.profiler.context.provider.stat.activethread;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.m2u.eyelink.agent.profiler.context.ActiveTraceHistogramFactory;
import com.m2u.eyelink.agent.profiler.context.active.ActiveTraceRepository;
import com.m2u.eyelink.agent.profiler.monitor.metric.activethread.ActiveTraceMetric;

public class ActiveTraceMetricProvider implements Provider<ActiveTraceMetric> {

    private final ActiveTraceRepository activeTraceRepository;

    @Inject
    public ActiveTraceMetricProvider(Provider<ActiveTraceRepository> activeTraceRepositoryProvider) {
        if (activeTraceRepositoryProvider == null) {
            throw new NullPointerException("activeTraceRepositoryProvider must not be null");
        }
        this.activeTraceRepository = activeTraceRepositoryProvider.get();
    }

    @Override
    public ActiveTraceMetric get() {
        if (activeTraceRepository == null) {
            return ActiveTraceMetric.UNSUPPORTED_ACTIVE_TRACE_METRIC;
        } else {
            ActiveTraceHistogramFactory activeTraceHistogramFactory = new ActiveTraceHistogramFactory(activeTraceRepository);
            return new DefaultActiveTraceMetric(activeTraceHistogramFactory);
        }
    }
}
