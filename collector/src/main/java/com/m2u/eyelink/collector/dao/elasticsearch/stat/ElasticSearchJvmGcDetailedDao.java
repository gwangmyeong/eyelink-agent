package com.m2u.eyelink.collector.dao.elasticsearch.stat;

import java.util.List;

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
import com.m2u.eyelink.util.CollectionUtils;

@Repository
public class ElasticSearchJvmGcDetailedDao implements AgentStatDaoV2<JvmGcDetailedBo> {

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
        List<Put> jvmGcDetailedPuts = this.agentStatElasticSearchOperationFactory.createPuts(agentId, AgentStatType.JVM_GC_DETAILED, jvmGcDetailedBos, this.jvmGcDetailedSerializer);
        if (!jvmGcDetailedPuts.isEmpty()) {
            List<Put> rejectedPuts = this.elasticSearchTemplate.asyncPut(ElasticSearchTables.AGENT_STAT_VER2, jvmGcDetailedPuts);
            if (CollectionUtils.isNotEmpty(rejectedPuts)) {
                this.elasticSearchTemplate.put(ElasticSearchTables.AGENT_STAT_VER2, rejectedPuts);
            }
        }
    }
}
