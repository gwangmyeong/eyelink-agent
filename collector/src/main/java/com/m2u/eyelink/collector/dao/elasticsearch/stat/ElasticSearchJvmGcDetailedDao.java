package com.m2u.eyelink.collector.dao.elasticsearch.stat;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.m2u.eyelink.collector.bo.serializer.stat.AgentStatElasticSearchOperationFactory;
import com.m2u.eyelink.collector.bo.serializer.stat.JvmGcDetailedSerializer;
import com.m2u.eyelink.collector.bo.stat.AgentStatType;
import com.m2u.eyelink.collector.bo.stat.JvmGcDetailedBo;
import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchOperations2;
import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchTables;
import com.m2u.eyelink.collector.common.elasticsearch.Put;
import com.m2u.eyelink.collector.dao.AgentStatDaoV2;
import com.m2u.eyelink.collector.handler.AgentStatHandlerV2;
import com.m2u.eyelink.collector.util.ElasticSearchUtils;
import com.m2u.eyelink.util.CollectionUtils;

@Repository
public class ElasticSearchJvmGcDetailedDao implements AgentStatDaoV2<JvmGcDetailedBo> {

	private final Logger logger = LoggerFactory.getLogger(AgentStatHandlerV2.class.getName());

    @Autowired
    private ElasticSearchOperations2 elasticSearchTemplate;

    @Autowired
    private AgentStatElasticSearchOperationFactory agentStatElasticSearchOperationFactory;

    @Autowired
    private JvmGcDetailedSerializer jvmGcDetailedSerializer;

    @Override
    public void insert(String agentId, List<JvmGcDetailedBo> jvmGcDetailedBos) {
        if (agentId == null) {
            throw new NullPointerException("agentId must not be null");
        }
        if (jvmGcDetailedBos == null || jvmGcDetailedBos.isEmpty()) {
            return;
        }

        List<Map<String, Object>> listJvmGcDetailBo = this.agentStatElasticSearchOperationFactory.createList(jvmGcDetailedBos);
        if (!listJvmGcDetailBo.isEmpty()) {
            boolean isSuccess = this.elasticSearchTemplate.asyncPut(ElasticSearchUtils.generateIndexName(agentId), ElasticSearchTables.TYPE_AGENT_STAT_JVMGC_DETAIL, listJvmGcDetailBo);
            if (!isSuccess) {
                this.elasticSearchTemplate.put(ElasticSearchUtils.generateIndexName(agentId), ElasticSearchTables.TYPE_AGENT_STAT_JVMGC_DETAIL, listJvmGcDetailBo);
            }
        } else {
        		logger.info("listJvmGcDetailBo is empty");
        }
        
    }
}
