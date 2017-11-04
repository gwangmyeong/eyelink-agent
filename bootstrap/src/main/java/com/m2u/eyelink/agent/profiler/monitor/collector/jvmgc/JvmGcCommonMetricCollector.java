package com.m2u.eyelink.agent.profiler.monitor.collector.jvmgc;

import com.m2u.eyelink.agent.profiler.monitor.metric.gc.GarbageCollectorMetric;
import com.m2u.eyelink.agent.profiler.monitor.metric.memory.MemoryMetric;
import com.m2u.eyelink.thrift.TJvmGc; 

public class JvmGcCommonMetricCollector implements JvmGcMetricCollector {

    private final MemoryMetric memoryMetric;

    private final GarbageCollectorMetric garbageCollectorMetric;

    public JvmGcCommonMetricCollector(MemoryMetric memoryMetric, GarbageCollectorMetric garbageCollectorMetric) {
        if (memoryMetric == null) {
            throw new NullPointerException("memoryMetric must not be null");
        }
        if (garbageCollectorMetric == null) {
            throw new NullPointerException("garbageCollectorMetric must not be null");
        }
        this.memoryMetric = memoryMetric;
        this.garbageCollectorMetric = garbageCollectorMetric;
    }

    @Override
    public TJvmGc collect() {
        TJvmGc jvmGc = new TJvmGc();
        jvmGc.setJvmMemoryHeapMax(memoryMetric.heapMax());
        jvmGc.setJvmMemoryHeapUsed(memoryMetric.heapUsed());
        jvmGc.setJvmMemoryNonHeapMax(memoryMetric.nonHeapMax());
        jvmGc.setJvmMemoryNonHeapUsed(memoryMetric.nonHeapUsed());
        jvmGc.setJvmGcOldCount(garbageCollectorMetric.gcOldCount());
        jvmGc.setJvmGcOldTime(garbageCollectorMetric.gcOldTime());
        jvmGc.setType(garbageCollectorMetric.gcType());
        return jvmGc;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("JvmGcCommonMetricCollector{");
        sb.append("memoryMetric=").append(memoryMetric);
        sb.append(", garbageCollectorMetric=").append(garbageCollectorMetric);
        sb.append('}');
        return sb.toString();
    }
}
