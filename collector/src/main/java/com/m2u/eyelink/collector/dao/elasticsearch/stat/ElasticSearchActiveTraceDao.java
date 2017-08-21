package com.m2u.eyelink.collector.dao.elasticsearch.stat;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.m2u.eyelink.collector.bo.serializer.stat.AgentStatElasticSearchOperationFactory;
import com.m2u.eyelink.collector.bo.stat.ActiveTraceBo;
import com.m2u.eyelink.collector.bo.stat.ActiveTraceSerializer;
import com.m2u.eyelink.collector.bo.stat.AgentStatType;
import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchOperations2;
import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchTables;
import com.m2u.eyelink.collector.common.elasticsearch.Put;
import com.m2u.eyelink.collector.dao.AgentStatDaoV2;
import com.m2u.eyelink.util.CollectionUtils;

@Repository
public class ElasticSearchActiveTraceDao implements AgentStatDaoV2<ActiveTraceBo> {

	@Autowired
	private ElasticSearchOperations2 elasticSearchTemplate;

	@Autowired
	private AgentStatElasticSearchOperationFactory agentStatElasticSearchOperationFactory;

	@Autowired
	private ActiveTraceSerializer activeTraceSerializer;

	@Override
	public void insert(String agentId, List<ActiveTraceBo> agentStatDataPoints) {
		if (agentId == null) {
			throw new NullPointerException("agentId must not be null");
		}
		if (agentStatDataPoints == null || agentStatDataPoints.isEmpty()) {
			return;
		}
		List<Put> activeTracePuts = this.agentStatElasticSearchOperationFactory.createPuts(agentId, AgentStatType.ACTIVE_TRACE,
				agentStatDataPoints, this.activeTraceSerializer);
		if (!activeTracePuts.isEmpty()) {
			List<Put> rejectedPuts = this.elasticSearchTemplate.asyncPut(ElasticSearchTables.AGENT_STAT_VER2, activeTracePuts);
			if (CollectionUtils.isNotEmpty(rejectedPuts)) {
				this.elasticSearchTemplate.put(ElasticSearchTables.AGENT_STAT_VER2, rejectedPuts);
			}
		}
	}
}
