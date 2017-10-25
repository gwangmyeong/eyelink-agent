package com.m2u.eyelink.collector.service;

import org.apache.thrift.TBase;

public interface AgentStatService {
    public void save(TBase<?, ?> tbase);
}
