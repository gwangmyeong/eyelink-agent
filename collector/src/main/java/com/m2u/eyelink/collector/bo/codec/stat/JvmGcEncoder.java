package com.m2u.eyelink.collector.bo.codec.stat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.m2u.eyelink.collector.bo.stat.JvmGcBo;

@Component
public class JvmGcEncoder extends AgentStatEncoder<JvmGcBo> {

    @Autowired
    public JvmGcEncoder(@Qualifier("jvmGcCodecV2") AgentStatCodec<JvmGcBo> jvmGcCodec) {
        super(jvmGcCodec);
    }
}
