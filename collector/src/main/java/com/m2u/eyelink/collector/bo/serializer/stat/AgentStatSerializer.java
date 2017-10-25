package com.m2u.eyelink.collector.bo.serializer.stat;

import java.nio.ByteBuffer;
import java.util.List;

import org.springframework.util.Assert;

import com.m2u.eyelink.collector.bo.codec.stat.AgentStatEncoder;
import com.m2u.eyelink.collector.bo.serializer.ElasticSearchSerializer;
import com.m2u.eyelink.collector.bo.serializer.SerializationContext;
import com.m2u.eyelink.collector.bo.stat.AgentStatDataPoint;
import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchTables;
import com.m2u.eyelink.collector.common.elasticsearch.HConstants;
import com.m2u.eyelink.collector.common.elasticsearch.Put;
import com.m2u.eyelink.util.CollectionUtils;

public abstract class AgentStatSerializer<T extends AgentStatDataPoint> implements ElasticSearchSerializer<List<T>, Put> {

    private final AgentStatEncoder<T> encoder;

    protected AgentStatSerializer(AgentStatEncoder<T> encoder) {
        Assert.notNull(encoder, "encoder must not be null");
        this.encoder = encoder;
    }

    @Override
    public void serialize(List<T> agentStatBos, Put put, SerializationContext context) {
        if (CollectionUtils.isEmpty(agentStatBos)) {
            throw new IllegalArgumentException("agentStatBos should not be empty");
        }
        long initialTimestamp = agentStatBos.get(0).getTimestamp();
        long baseTimestamp = AgentStatUtils.getBaseTimestamp(initialTimestamp);
        long timestampDelta = initialTimestamp - baseTimestamp;
        ByteBuffer qualifierBuffer = this.encoder.encodeQualifier(timestampDelta);
        ByteBuffer valueBuffer = this.encoder.encodeValue(agentStatBos);
        put.addColumn(ElasticSearchTables.AGENT_STAT_CF_STATISTICS, qualifierBuffer, HConstants.LATEST_TIMESTAMP, valueBuffer);
    }
}
