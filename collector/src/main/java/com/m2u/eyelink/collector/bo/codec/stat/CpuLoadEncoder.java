package com.m2u.eyelink.collector.bo.codec.stat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.m2u.eyelink.collector.bo.stat.CpuLoadBo;

@Component
public class CpuLoadEncoder extends AgentStatEncoder<CpuLoadBo> {

    @Autowired
    private CpuLoadEncoder(@Qualifier("cpuLoadCodecV2") AgentStatCodec<CpuLoadBo> cpuLoadCodec) {
        super(cpuLoadCodec);
    }
}
