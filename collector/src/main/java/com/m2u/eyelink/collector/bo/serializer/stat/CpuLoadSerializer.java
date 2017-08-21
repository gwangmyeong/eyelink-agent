package com.m2u.eyelink.collector.bo.serializer.stat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.m2u.eyelink.collector.bo.codec.stat.CpuLoadEncoder;
import com.m2u.eyelink.collector.bo.stat.CpuLoadBo;

@Component
public class CpuLoadSerializer extends AgentStatSerializer<CpuLoadBo> {

    @Autowired
    public CpuLoadSerializer(CpuLoadEncoder cpuLoadEncoder) {
        super(cpuLoadEncoder);
    }
}