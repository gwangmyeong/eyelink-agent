package com.m2u.eyelink.collector.bo.serializer.stat;

import org.springframework.stereotype.Component;

import com.m2u.eyelink.collector.bo.serializer.RowKeyDecoder;
import com.m2u.eyelink.collector.bo.stat.AgentStatType;
import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchTables;
import com.m2u.eyelink.collector.util.TimeUtils;
import com.m2u.eyelink.util.BytesUtils;

@Component
public class AgentStatRowKeyDecoder implements RowKeyDecoder<AgentStatRowKeyComponent> {

    @Override
    public AgentStatRowKeyComponent decodeRowKey(byte[] rowkey) {
        final String agentId = BytesUtils.safeTrim(BytesUtils.toString(rowkey, 0, ElasticSearchTables.AGENT_NAME_MAX_LEN));
        final AgentStatType agentStatType = AgentStatType.fromTypeCode(rowkey[ElasticSearchTables.AGENT_NAME_MAX_LEN]);
        final long reversedBaseTimestamp = BytesUtils.bytesToLong(rowkey, ElasticSearchTables.AGENT_NAME_MAX_LEN + ElasticSearchTables.TYPE_CODE_BYTE_LENGTH);
        final long baseTimestamp = TimeUtils.recoveryTimeMillis(reversedBaseTimestamp);
        return new AgentStatRowKeyComponent(agentId, agentStatType, baseTimestamp);
    }
}
