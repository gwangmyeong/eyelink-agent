package com.m2u.eyelink.agent.profiler.monitor.collector.cpu;

import com.m2u.eyelink.agent.profiler.monitor.metric.cpu.CpuLoadMetric;
import com.m2u.eyelink.context.thrift.TCpuLoad;

public class DefaultCpuLoadMetricCollector implements CpuLoadMetricCollector {

    private final CpuLoadMetric cpuLoadMetric;

    public DefaultCpuLoadMetricCollector(CpuLoadMetric cpuLoadMetric) {
        if (cpuLoadMetric == null) {
            throw new NullPointerException("cpuLoadMetric must not be null");
        }
        this.cpuLoadMetric = cpuLoadMetric;
    }

    @Override
    public TCpuLoad collect() {
        TCpuLoad cpuLoad = new TCpuLoad();
        cpuLoad.setJvmCpuLoad(cpuLoadMetric.jvmCpuLoad());
        cpuLoad.setSystemCpuLoad(cpuLoadMetric.systemCpuLoad());
        return cpuLoad;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DefaultCpuLoadMetricCollector{");
        sb.append("cpuLoadMetric=").append(cpuLoadMetric);
        sb.append('}');
        return sb.toString();
    }
}
