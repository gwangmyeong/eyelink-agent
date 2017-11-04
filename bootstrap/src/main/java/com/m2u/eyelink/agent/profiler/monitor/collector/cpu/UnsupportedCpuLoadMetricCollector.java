package com.m2u.eyelink.agent.profiler.monitor.collector.cpu;

import com.m2u.eyelink.thrift.TCpuLoad; 

public class UnsupportedCpuLoadMetricCollector implements CpuLoadMetricCollector {

    @Override
    public TCpuLoad collect() {
        return null;
    }

    @Override
    public String toString() {
        return "UnsupportedCpuLoadMetricCollector";
    }
}

