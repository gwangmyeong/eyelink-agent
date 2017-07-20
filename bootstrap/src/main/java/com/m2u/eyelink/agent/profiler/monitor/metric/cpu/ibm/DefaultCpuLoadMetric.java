package com.m2u.eyelink.agent.profiler.monitor.metric.cpu.ibm;

import java.lang.management.ManagementFactory;
// FIXME oracle 것을 일단 사용함. -> ibm 것으로 변경을 위한 작업 필요함.
//import com.ibm.lang.management.OperatingSystemMXBean;
import com.sun.management.OperatingSystemMXBean;

import com.codahale.metrics.Gauge;
import com.m2u.eyelink.agent.profiler.monitor.metric.cpu.CpuLoadMetric;

/**
 * @author HyunGil Jeong
 */
public class DefaultCpuLoadMetric implements CpuLoadMetric {

    private final Gauge<Double> jvmCpuLoadGauge;
    private final Gauge<Double> systemCpuLoadGauge;

    public DefaultCpuLoadMetric() {
        final OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        jvmCpuLoadGauge = new Gauge<Double>() {
            @Override
            public Double getValue() {
                return operatingSystemMXBean.getProcessCpuLoad();
            }
        };
        systemCpuLoadGauge = new Gauge<Double>() {
            @Override
            public Double getValue() {
                return operatingSystemMXBean.getSystemCpuLoad();
            }
        };
    }

    @Override
    public Double jvmCpuLoad() {
        return jvmCpuLoadGauge.getValue();
    }

    @Override
    public Double systemCpuLoad() {
        return systemCpuLoadGauge.getValue();
    }

    @Override
    public String toString() {
        return "CpuLoadMetric for IBM Java 1.7+";
    }
}
