package com.m2u.eyelink.collector.bo.serializer.stat;

import org.springframework.stereotype.Component;

import com.m2u.eyelink.collector.bo.serializer.RowKeyEncoder;
import com.m2u.eyelink.collector.util.TimeUtils;
import com.m2u.eyelink.util.BytesUtils;

@Component
public class AgentStatRowKeyEncoder implements RowKeyEncoder<AgentStatRowKeyComponent> {

    @Override
    public byte[] encodeRowKey(AgentStatRowKeyComponent component) {
        if (component == null) {
            throw new NullPointerException("component must not be null");
        }
        // FIXME, commented logic by bsh
//        byte[] bAgentId = BytesUtils.toBytes(component.getAgentId());
//        byte[] bStatType = new byte[]{component.getAgentStatType().getRawTypeCode()};
//        byte[] rowKey = new byte[AGENT_NAME_MAX_LEN + bStatType.length + BytesUtils.LONG_BYTE_LENGTH];
//
//        BytesUtils.writeBytes(rowKey, 0, bAgentId);
//        BytesUtils.writeBytes(rowKey, AGENT_NAME_MAX_LEN, bStatType);
//        BytesUtils.writeLong(TimeUtils.reverseTimeMillis(component.getBaseTimestamp()), rowKey, AGENT_NAME_MAX_LEN + bStatType.length);

        return null;
    }
}
