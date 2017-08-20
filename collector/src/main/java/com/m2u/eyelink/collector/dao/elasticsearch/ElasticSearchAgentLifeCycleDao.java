package com.m2u.eyelink.collector.dao.elasticsearch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.m2u.eyelink.collector.bo.AgentLifeCycleBo;
import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchOperations2;
import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchTables;
import com.m2u.eyelink.collector.dao.AgentLifeCycleDao;
import com.m2u.eyelink.collector.dao.elasticsearch.mapper.AgentLifeCycleValueMapper;
import com.m2u.eyelink.collector.util.ElasticSearchUtils;
import com.m2u.eyelink.collector.util.TimeUtils;
import com.m2u.eyelink.util.BytesUtils;

@Repository
public class ElasticSearchAgentLifeCycleDao implements AgentLifeCycleDao {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ElasticSearchOperations2 elasticSearchTemplate;

	@Autowired
	private AgentLifeCycleValueMapper valueMapper;

	@Override
	public void insert(AgentLifeCycleBo agentLifeCycleBo) {
		if (agentLifeCycleBo == null) {
			throw new NullPointerException("agentLifeCycleBo must not be null");
		}

		if (logger.isDebugEnabled()) {
			logger.debug("insert agent life cycle. {}", agentLifeCycleBo.toString());
		}

		final String agentId = agentLifeCycleBo.getAgentId();
		final long startTimestamp = agentLifeCycleBo.getStartTimestamp();
		final long eventIdentifier = agentLifeCycleBo.getEventIdentifier();

		// byte[] rowKey = createRowKey(agentId, startTimestamp, eventIdentifier);

//		this.elasticSearchTemplate.put(ElasticSearchTables.AGENT_LIFECYCLE, rowKey,
//				ElasticSearchTables.AGENT_LIFECYCLE_CF_STATUS,
//				ElasticSearchTables.AGENT_LIFECYCLE_CF_STATUS_QUALI_STATES, agentLifeCycleBo, this.valueMapper);
		this.elasticSearchTemplate.put(ElasticSearchUtils.generateIndexName(agentId),
				ElasticSearchTables.TYPE_AGENT_LIFECYCLE, agentLifeCycleBo.getMap());
	}

	byte[] createRowKey(String agentId, long startTimestamp, long eventIdentifier) {
		byte[] agentIdKey = Bytes.toBytes(agentId);
		long reverseStartTimestamp = TimeUtils.reverseTimeMillis(startTimestamp);
		long reverseEventCounter = TimeUtils.reverseTimeMillis(eventIdentifier);

		byte[] rowKey = new byte[ElasticSearchTables.AGENT_NAME_MAX_LEN + BytesUtils.LONG_BYTE_LENGTH
				+ BytesUtils.LONG_BYTE_LENGTH];
		BytesUtils.writeBytes(rowKey, 0, agentIdKey);
		int offset = ElasticSearchTables.AGENT_NAME_MAX_LEN;
		BytesUtils.writeLong(reverseStartTimestamp, rowKey, offset);
		offset += BytesUtils.LONG_BYTE_LENGTH;
		BytesUtils.writeLong(reverseEventCounter, rowKey, offset);

		return rowKey;
	}

}
