package com.m2u.eyelink.collector.bo.serializer.stat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.m2u.eyelink.collector.bo.codec.stat.JvmGcEncoder;
import com.m2u.eyelink.collector.bo.stat.JvmGcBo;

@Component
public class JvmGcSerializer extends AgentStatSerializer<JvmGcBo> {

    @Autowired
    public JvmGcSerializer(JvmGcEncoder jvmGcEncoder) {
        super(jvmGcEncoder);
    }
}