package com.m2u.eyelink.collector.bo.codec.stat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.m2u.eyelink.collector.bo.stat.JvmGcDetailedBo;

@Component
public class JvmGcDetailedEncoder extends AgentStatEncoder<JvmGcDetailedBo> {

    @Autowired
    public JvmGcDetailedEncoder(@Qualifier("jvmGcDetailedCodecV2") AgentStatCodec<JvmGcDetailedBo> jvmGcDetailedCodec) {
        super(jvmGcDetailedCodec);
    }
}
