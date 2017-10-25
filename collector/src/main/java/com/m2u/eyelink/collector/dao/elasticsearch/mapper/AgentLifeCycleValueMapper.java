package com.m2u.eyelink.collector.dao.elasticsearch.mapper;

import org.springframework.stereotype.Component;

import com.m2u.eyelink.collector.bo.AgentLifeCycleBo;
import com.m2u.eyelink.collector.common.elasticsearch.ValueMapper;
import com.m2u.eyelink.util.AutomaticBuffer;
import com.m2u.eyelink.util.Buffer;

@Component
public class AgentLifeCycleValueMapper implements ValueMapper<AgentLifeCycleBo> {

    @Override
    public byte[] mapValue(AgentLifeCycleBo value) {
        final Buffer buffer = new AutomaticBuffer();
        buffer.putInt(value.getVersion());
        buffer.putPrefixedString(value.getAgentId());
        buffer.putLong(value.getStartTimestamp());
        buffer.putLong(value.getEventTimestamp());
        buffer.putLong(value.getEventIdentifier());
        buffer.putShort(value.getAgentLifeCycleState().getCode());
        return buffer.getBuffer();
    }

}
