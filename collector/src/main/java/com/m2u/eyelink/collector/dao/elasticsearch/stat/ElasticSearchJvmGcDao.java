package com.m2u.eyelink.collector.dao.elasticsearch.stat;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.m2u.eyelink.collector.bo.serializer.stat.AgentStatElasticSearchOperationFactory;
import com.m2u.eyelink.collector.bo.serializer.stat.JvmGcSerializer;
import com.m2u.eyelink.collector.bo.stat.AgentStatType;
import com.m2u.eyelink.collector.bo.stat.JvmGcBo;
import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchOperations2;
import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchTables;
import com.m2u.eyelink.collector.common.elasticsearch.Put;
import com.m2u.eyelink.collector.dao.AgentStatDaoV2;
import com.m2u.eyelink.util.CollectionUtils;

@Repository
public class ElasticSearchJvmGcDao implements AgentStatDaoV2<JvmGcBo> {

    @Autowired
    private ElasticSearchOperations2 elasticSearchTemplate;

    @Autowired
    private AgentStatElasticSearchOperationFactory agentStatElasticSearchOperationFactory;

    @Autowired
    private JvmGcSerializer jvmGcSerializer;

    @Override
    public void insert(String agentId, List<JvmGcBo> jvmGcBos) {
        if (agentId == null) {
            throw new NullPointerException("agentId must not be null");
        }
        if (jvmGcBos == null || jvmGcBos.isEmpty()) {
            return;
        }
        List<Put> jvmGcBoPuts = this.agentStatElasticSearchOperationFactory.createPuts(agentId, AgentStatType.JVM_GC, jvmGcBos, this.jvmGcSerializer);
        if (!jvmGcBoPuts.isEmpty()) {
            List<Put> rejectedPuts = this.elasticSearchTemplate.asyncPut(ElasticSearchTables.AGENT_STAT_VER2, jvmGcBoPuts);
            if (CollectionUtils.isNotEmpty(rejectedPuts)) {
                this.elasticSearchTemplate.put(ElasticSearchTables.AGENT_STAT_VER2, rejectedPuts);
            }
        }
    }
}
