package com.m2u.eyelink.agent.profiler.monitor.metric.activethread;

import com.m2u.eyelink.context.thrift.TActiveTraceHistogram;

public interface ActiveTraceMetric {

    ActiveTraceMetric UNSUPPORTED_ACTIVE_TRACE_METRIC = new ActiveTraceMetric() {
        @Override
        public TActiveTraceHistogram activeTraceHistogram() {
            return null;
        }

        @Override
        public String toString() {
            return "Unsupported ActiveTraceMetric";
        }
    };

    TActiveTraceHistogram activeTraceHistogram();
}

