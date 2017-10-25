package com.m2u.eyelink.collector.dao.elasticsearch;

import static com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchTables.APPLICATION_INDEX;
import static com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchTables.APPLICATION_INDEX_CF_AGENTS;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchOperations2;
import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchTables;
import com.m2u.eyelink.collector.common.elasticsearch.Put;
import com.m2u.eyelink.collector.dao.ApplicationIndexDao;
import com.m2u.eyelink.collector.util.ElasticSearchUtils;
import com.m2u.eyelink.context.thrift.TAgentInfo;

@Repository
public class ElasticSearchApplicationIndexDao implements ApplicationIndexDao {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ElasticSearchOperations2 elasticSearchTemplate;

    @Override
    public void insert(final TAgentInfo agentInfo) {
        if (agentInfo == null) {
            throw new NullPointerException("agentInfo must not be null");
        }

//        Put put = new Put(Bytes.toBytes(agentInfo.getApplicationName()));
//        byte[] qualifier = Bytes.toBytes(agentInfo.getAgentId());
//        byte[] value = Bytes.toBytes(agentInfo.getServiceType());
//        
//        put.addColumn(APPLICATION_INDEX_CF_AGENTS, qualifier, value);
//        
//        elasticSearchTemplate.put(APPLICATION_INDEX, put);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("agentId", agentInfo.getAgentId());
        map.put("applicationName", agentInfo.getApplicationName());
        map.put("serviceType", agentInfo.getServiceType());
        elasticSearchTemplate.put(ElasticSearchUtils.generateIndexName(agentInfo.getAgentId()), ElasticSearchTables.TYPE_APPLICATION_INDEX, map);
        
        logger.debug("Insert agentInfo. {}", agentInfo);
    }
}
