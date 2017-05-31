package com.m2u.eyelink.agent.profiler.monitor.collector;

import org.apache.thrift.TBase;
import org.apache.thrift.TFieldIdEnum;

public interface AgentStatMetricCollector<T extends TBase<T, ? extends TFieldIdEnum>> {
    
    T collect();

}