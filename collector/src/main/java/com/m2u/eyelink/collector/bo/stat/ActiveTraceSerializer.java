package com.m2u.eyelink.collector.bo.stat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.m2u.eyelink.collector.bo.codec.stat.ActiveTraceEncoder;
import com.m2u.eyelink.collector.bo.serializer.stat.AgentStatSerializer;

@Component
public class ActiveTraceSerializer extends AgentStatSerializer<ActiveTraceBo> {

    @Autowired
    public ActiveTraceSerializer(ActiveTraceEncoder activeTraceEncoder) {
        super(activeTraceEncoder);
    }
}
