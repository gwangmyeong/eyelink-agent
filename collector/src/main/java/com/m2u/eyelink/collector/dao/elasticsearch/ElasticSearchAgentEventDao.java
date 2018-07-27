package com.m2u.eyelink.collector.dao.elasticsearch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.m2u.eyelink.collector.bo.AgentEventBo;
import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchOperations2;
import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchTables;
import com.m2u.eyelink.collector.dao.AgentEventDao;
import com.m2u.eyelink.collector.dao.elasticsearch.mapper.AgentEventValueMapper;
import com.m2u.eyelink.collector.util.AgentEventType;
import com.m2u.eyelink.collector.util.ElasticSearchUtils;
import com.m2u.eyelink.collector.util.RowKeyUtils;
import com.m2u.eyelink.collector.util.TimeUtils;
import com.m2u.eyelink.util.BytesUtils;

@Repository
public class ElasticSearchAgentEventDao implements AgentEventDao {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ElasticSearchOperations2 elasticSearchTemplate;

    @Autowired
    private AgentEventValueMapper valueMapper;

    @Override
    public void insert(AgentEventBo agentEventBo) {
        if (agentEventBo == null) {
            throw new NullPointerException("agentEventBo must not be null");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("insert event. {}", agentEventBo.toString());
        }

        final String agentId = agentEventBo.getAgentId();
        final long eventTimestamp = agentEventBo.getEventTimestamp();

        byte[] rowKey = createRowKey(agentId, eventTimestamp);

        final AgentEventType eventType = agentEventBo.getEventType();
        byte[] qualifier = Bytes.toBytes(eventType.getCode());

//        this.elasticSearchTemplate.put(ElasticSearchTables.AGENT_EVENT, rowKey, ElasticSearchTables.AGENT_EVENT_CF_EVENTS, qualifier, agentEventBo, this.valueMapper);
		this.elasticSearchTemplate.put(ElasticSearchUtils.generateIndexName(agentEventBo.getAgentId(), ElasticSearchTables.TYPE_AGENT_EVENT),
				ElasticSearchTables.TYPE_AGENT_EVENT, agentEventBo.getMap());

    }

    byte[] createRowKey(String agentId, long eventTimestamp) {
        byte[] agentIdKey = BytesUtils.toBytes(agentId);
        long reverseStartTimestamp = TimeUtils.reverseTimeMillis(eventTimestamp);
        return RowKeyUtils.concatFixedByteAndLong(agentIdKey, ElasticSearchTables.AGENT_NAME_MAX_LEN, reverseStartTimestamp);
    }

}
