package com.m2u.eyelink.collector.dao.elasticsearch.stat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.m2u.eyelink.collector.bo.serializer.stat.AgentStatElasticSearchOperationFactory;
import com.m2u.eyelink.collector.bo.serializer.stat.AgentStatUtils;
import com.m2u.eyelink.collector.bo.serializer.stat.DataSourceSerializer;
import com.m2u.eyelink.collector.bo.stat.AgentStatType;
import com.m2u.eyelink.collector.bo.stat.DataSourceBo;
import com.m2u.eyelink.collector.bo.stat.DataSourceListBo;
import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchOperations2;
import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchTables;
import com.m2u.eyelink.collector.common.elasticsearch.Put;
import com.m2u.eyelink.collector.dao.AgentStatDaoV2;
import com.m2u.eyelink.collector.util.ElasticSearchUtils;
import com.m2u.eyelink.util.CollectionUtils;

@Repository
public class ElasticSearchDataSourceListDao implements AgentStatDaoV2<DataSourceListBo> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ElasticSearchOperations2 elasticSearchTemplate;

    @Autowired
    private AgentStatElasticSearchOperationFactory agentStatElasticSearchOperationFactory;

    @Autowired
    private DataSourceSerializer dataSourceSerializer;

    @Override
    public void insert(String agentId, List<DataSourceListBo> dataSourceListBos) {
        if (agentId == null) {
            throw new NullPointerException("agentId must not be null");
        }
        if (CollectionUtils.isEmpty(dataSourceListBos)) {
            return;
        }

        List<DataSourceListBo> reorderedDataSourceListBos = reorderDataSourceListBos(dataSourceListBos);
        if (reorderedDataSourceListBos != null) {
	        	List<Map<String, Object>> listDataSourceListBos = this.agentStatElasticSearchOperationFactory.createList(reorderedDataSourceListBos);
	        	if (!listDataSourceListBos.isEmpty()) {
	        		boolean isSuccess = this.elasticSearchTemplate.asyncPut(ElasticSearchUtils.generateIndexName(agentId), ElasticSearchTables.TYPE_AGENT_STAT_DATA_SOURCE, listDataSourceListBos);
	        		if (!isSuccess) {
	        			this.elasticSearchTemplate.put(ElasticSearchUtils.generateIndexName(agentId), ElasticSearchTables.TYPE_AGENT_STAT_DATA_SOURCE, listDataSourceListBos);
	        		}
	        	} else {
	        		logger.info("listDataSourceListBos is empty");
	        	}
        } else {
        		logger.info("reorderedDataSourceListBos is empty");
        }
    }

    private List<DataSourceListBo> reorderDataSourceListBos(List<DataSourceListBo> dataSourceListBos) {
        // reorder dataSourceBo using id and timeSlot
        MultiKeyMap dataSourceListBoMap = new MultiKeyMap();

        for (DataSourceListBo dataSourceListBo : dataSourceListBos) {
            for (DataSourceBo dataSourceBo : dataSourceListBo.getList()) {
                int id = dataSourceBo.getId();
                long timestamp = dataSourceBo.getTimestamp();
                long timeSlot = AgentStatUtils.getBaseTimestamp(timestamp);

                DataSourceListBo mappedDataSourceListBo = (DataSourceListBo) dataSourceListBoMap.get(id, timeSlot);
                if (mappedDataSourceListBo == null) {
                    mappedDataSourceListBo = new DataSourceListBo();
                    mappedDataSourceListBo.setAgentId(dataSourceBo.getAgentId());
                    mappedDataSourceListBo.setStartTimestamp(dataSourceBo.getStartTimestamp());
                    mappedDataSourceListBo.setTimestamp(dataSourceBo.getTimestamp());

                    dataSourceListBoMap.put(id, timeSlot, mappedDataSourceListBo);
                }

                // set fastest timestamp
                if (mappedDataSourceListBo.getTimestamp() > dataSourceBo.getTimestamp()) {
                    mappedDataSourceListBo.setTimestamp(dataSourceBo.getTimestamp());
                }

                mappedDataSourceListBo.add(dataSourceBo);
            }
        }

        if (dataSourceListBoMap.values() == null) {
        		return null;
        } else {
	        	Collection values = dataSourceListBoMap.values();
	        	return new ArrayList<DataSourceListBo>(values);
        }
    }

}
