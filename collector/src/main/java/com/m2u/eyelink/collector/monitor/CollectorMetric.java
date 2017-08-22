package com.m2u.eyelink.collector.monitor;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.codahale.metrics.JvmAttributeGaugeSet;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.m2u.eyelink.collector.util.LoggerUtils;

@Component
public class CollectorMetric {

    public static final String REPORTER_LOGGER_NAME = "com.m2u.eyelink.collector.StateReport";

    private final Logger reporterLogger = LoggerFactory.getLogger(REPORTER_LOGGER_NAME);

    @Autowired
    private MetricRegistry metricRegistry;

    @Autowired(required = false)
    private ElasticSearchAsyncOperationMetrics elasticSearchAsyncOperationMetrics;

    private ScheduledReporter reporter;

    private final boolean isEnable = isEnable0(REPORTER_LOGGER_NAME);

    @PostConstruct
    public void start() {
        initRegistry();
        initReporters();
    }

    public boolean isEnable() {
        return isEnable;
    }

    private boolean isEnable0(String loggerName) {
        final Logger logger = LoggerFactory.getLogger(loggerName);
        final int loggerLevel = LoggerUtils.getLoggerLevel(logger);
        if (loggerLevel >= LoggerUtils.WARN_LEVEL) {
            return false;
        }
        return true;
    }

    private void initRegistry() {
        // add JVM statistics
        metricRegistry.register("jvm.memory", new MemoryUsageGaugeSet());
        metricRegistry.register("jvm.vm", new JvmAttributeGaugeSet());
        metricRegistry.register("jvm.garbage-collectors", new GarbageCollectorMetricSet());
        metricRegistry.register("jvm.thread-states", new ThreadStatesGaugeSet());

        if (elasticSearchAsyncOperationMetrics != null) {
            Map<String, Metric> metrics = elasticSearchAsyncOperationMetrics.getMetrics();
            for (Map.Entry<String, Metric> metric : metrics.entrySet()) {
                metricRegistry.register(metric.getKey(), metric.getValue());
            }
        }
    }

    private void initReporters() {
        Slf4jReporter.Builder builder = Slf4jReporter.forRegistry(metricRegistry);
        builder.convertRatesTo(TimeUnit.SECONDS);
        builder.convertDurationsTo(TimeUnit.MILLISECONDS);

        builder.outputTo(reporterLogger);
        reporter = builder.build();

        reporter.start(60, TimeUnit.SECONDS); // print every 1 min.
    }


    @PreDestroy
    private void shutdown() {
        if (reporter == null) {
            return;
        }
        reporter.stop();
        reporter = null;
    }

}
