package com.m2u.eyelink.collector.bo.codec.stat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.m2u.eyelink.collector.bo.stat.ActiveTraceBo;

@Component
public class ActiveTraceEncoder extends AgentStatEncoder<ActiveTraceBo> {

    @Autowired
    public ActiveTraceEncoder(@Qualifier("activeTraceCodecV2") AgentStatCodec<ActiveTraceBo> activeTraceCodec) {
        super(activeTraceCodec);
    }
}
