package com.m2u.eyelink.agent.profiler.context.provider.stat.activethread;

import java.util.List;

import com.m2u.eyelink.agent.profiler.context.ActiveTraceHistogramFactory;
import com.m2u.eyelink.agent.profiler.context.ActiveTraceHistogramFactory.ActiveTraceHistogram;
import com.m2u.eyelink.agent.profiler.monitor.metric.activethread.ActiveTraceMetric;
import com.m2u.eyelink.thrift.TActiveTraceHistogram; class DefaultActiveTraceMetric implements ActiveTraceMetric {

    private final ActiveTraceHistogramFactory activeTraceHistogramFactory;

    public DefaultActiveTraceMetric(ActiveTraceHistogramFactory activeTraceHistogramFactory) {
        if (activeTraceHistogramFactory == null) {
            throw new NullPointerException("activeTraceHistogramFactory must not be null");
        }
        this.activeTraceHistogramFactory = activeTraceHistogramFactory;
    }

    @Override
    public TActiveTraceHistogram activeTraceHistogram() {
        ActiveTraceHistogram activeTraceHistogram = activeTraceHistogramFactory.createHistogram();
        int histogramSchemaTypeCode = activeTraceHistogram.getHistogramSchema().getTypeCode();
        List<Integer> activeTraceCounts = activeTraceHistogram.getActiveTraceCounts();

        TActiveTraceHistogram tActiveTraceHistogram = new TActiveTraceHistogram();
        tActiveTraceHistogram.setHistogramSchemaType(histogramSchemaTypeCode);
        tActiveTraceHistogram.setActiveTraceCount(activeTraceCounts);
        return tActiveTraceHistogram;
    }

    @Override
    public String toString() {
        return "Default ActiveTraceCountMetric";
    }
}
