package com.m2u.eyelink.collector.dao.elasticsearch.stat;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.m2u.eyelink.collector.handler.AgentStatHandlerV2;
import com.m2u.eyelink.collector.util.ElasticSearchUtils;
import com.m2u.eyelink.util.CollectionUtils;

@Repository
public class ElasticSearchActiveTraceDao implements AgentStatDaoV2<ActiveTraceBo> {

	private final Logger logger = LoggerFactory.getLogger(AgentStatHandlerV2.class.getName());
	
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

        List<Map<String, Object>> listActiveTraceBos = this.agentStatElasticSearchOperationFactory.createList(agentStatDataPoints);
        if (!listActiveTraceBos.isEmpty()) {
            boolean isSuccess = this.elasticSearchTemplate.asyncPut(ElasticSearchUtils.generateIndexName(agentId, ElasticSearchTables.TYPE_AGENT_STAT_ACTIVE_TRACE), ElasticSearchTables.TYPE_AGENT_STAT_ACTIVE_TRACE, listActiveTraceBos);
            if (!isSuccess) {
                this.elasticSearchTemplate.put(ElasticSearchUtils.generateIndexName(agentId, ElasticSearchTables.TYPE_AGENT_STAT_ACTIVE_TRACE), ElasticSearchTables.TYPE_AGENT_STAT_ACTIVE_TRACE, listActiveTraceBos);
            }
        } else {
        		logger.info("listActiveTraceBos is empty");
        }
        
	}
}
