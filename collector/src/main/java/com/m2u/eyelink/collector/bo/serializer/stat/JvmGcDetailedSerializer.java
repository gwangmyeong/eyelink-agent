package com.m2u.eyelink.collector.bo.serializer.stat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.m2u.eyelink.collector.bo.codec.stat.JvmGcDetailedEncoder;
import com.m2u.eyelink.collector.bo.stat.JvmGcDetailedBo;

@Component
public class JvmGcDetailedSerializer extends AgentStatSerializer<JvmGcDetailedBo> {

    @Autowired
    public JvmGcDetailedSerializer(JvmGcDetailedEncoder jvmGcDetailedEncoder) {
        super(jvmGcDetailedEncoder);
    }
}
