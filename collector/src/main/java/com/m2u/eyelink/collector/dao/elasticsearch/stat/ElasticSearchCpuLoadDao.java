package com.m2u.eyelink.collector.dao.elasticsearch.stat;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.m2u.eyelink.collector.bo.serializer.stat.AgentStatElasticSearchOperationFactory;
import com.m2u.eyelink.collector.bo.serializer.stat.CpuLoadSerializer;
import com.m2u.eyelink.collector.bo.stat.AgentStatType;
import com.m2u.eyelink.collector.bo.stat.CpuLoadBo;
import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchOperations2;
import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchTables;
import com.m2u.eyelink.collector.common.elasticsearch.Put;
import com.m2u.eyelink.collector.dao.AgentStatDaoV2;
import com.m2u.eyelink.util.CollectionUtils;

@Repository
public class ElasticSearchCpuLoadDao implements AgentStatDaoV2<CpuLoadBo> {

    @Autowired
    private ElasticSearchOperations2 elasticSearchTemplate;

    @Autowired
    private AgentStatElasticSearchOperationFactory agentStatElasticSearchOperationFactory;

    @Autowired
    private CpuLoadSerializer cpuLoadSerializer;

    @Override
    public void insert(String agentId, List<CpuLoadBo> cpuLoadBos) {
        if (agentId == null) {
            throw new NullPointerException("agentId must not be null");
        }
        if (cpuLoadBos == null || cpuLoadBos.isEmpty()) {
            return;
        }
        List<Put> cpuLoadPuts = this.agentStatElasticSearchOperationFactory.createPuts(agentId, AgentStatType.CPU_LOAD, cpuLoadBos, this.cpuLoadSerializer);
        if (!cpuLoadPuts.isEmpty()) {
            List<Put> rejectedPuts = this.elasticSearchTemplate.asyncPut(ElasticSearchTables.AGENT_STAT_VER2, cpuLoadPuts);
            if (CollectionUtils.isNotEmpty(rejectedPuts)) {
                this.elasticSearchTemplate.put(ElasticSearchTables.AGENT_STAT_VER2, rejectedPuts);
            }
        }
    }
}
