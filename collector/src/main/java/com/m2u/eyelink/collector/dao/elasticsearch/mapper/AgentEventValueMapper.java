package com.m2u.eyelink.collector.dao.elasticsearch.mapper;

import org.springframework.stereotype.Component;

import com.m2u.eyelink.collector.bo.AgentEventBo;
import com.m2u.eyelink.collector.common.elasticsearch.ValueMapper;
import com.m2u.eyelink.util.AutomaticBuffer;
import com.m2u.eyelink.util.Buffer;

@Component
public class AgentEventValueMapper implements ValueMapper<AgentEventBo> {

    @Override
    public byte[] mapValue(AgentEventBo value) {
        final Buffer buffer = new AutomaticBuffer();
        buffer.putInt(value.getVersion());
        buffer.putPrefixedString(value.getAgentId());
        buffer.putLong(value.getStartTimestamp());
        buffer.putLong(value.getEventTimestamp());
        buffer.putPrefixedBytes(value.getEventBody());
        return buffer.getBuffer();
    }

}
