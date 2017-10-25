package com.m2u.eyelink.agent.profiler.context.provider.stat.cpu;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.m2u.eyelink.agent.profiler.monitor.collector.cpu.CpuLoadMetricCollector;
import com.m2u.eyelink.agent.profiler.monitor.collector.cpu.DefaultCpuLoadMetricCollector;
import com.m2u.eyelink.agent.profiler.monitor.collector.cpu.UnsupportedCpuLoadMetricCollector;
import com.m2u.eyelink.agent.profiler.monitor.metric.cpu.CpuLoadMetric;

public class CpuLoadMetricCollectorProvider implements Provider<CpuLoadMetricCollector> {

    private final CpuLoadMetric cpuLoadMetric;

    @Inject
    public CpuLoadMetricCollectorProvider(CpuLoadMetric cpuLoadMetric) {
        if (cpuLoadMetric == null) {
            throw new NullPointerException("cpuLoadMetric must not be null");
        }
        this.cpuLoadMetric = cpuLoadMetric;
    }

    @Override
    public CpuLoadMetricCollector get() {
        if (cpuLoadMetric == CpuLoadMetric.UNSUPPORTED_CPU_LOAD_METRIC) {
            return new UnsupportedCpuLoadMetricCollector();
        }
        return new DefaultCpuLoadMetricCollector(cpuLoadMetric);
    }
}
