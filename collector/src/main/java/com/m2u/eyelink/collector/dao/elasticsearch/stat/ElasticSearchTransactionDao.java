package com.m2u.eyelink.collector.dao.elasticsearch.stat;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.m2u.eyelink.collector.bo.serializer.stat.AgentStatElasticSearchOperationFactory;
import com.m2u.eyelink.collector.bo.serializer.stat.TransactionSerializer;
import com.m2u.eyelink.collector.bo.stat.AgentStatType;
import com.m2u.eyelink.collector.bo.stat.TransactionBo;
import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchOperations2;
import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchTables;
import com.m2u.eyelink.collector.common.elasticsearch.Put;
import com.m2u.eyelink.collector.dao.AgentStatDaoV2;
import com.m2u.eyelink.util.CollectionUtils;

@Repository
public class ElasticSearchTransactionDao  implements AgentStatDaoV2<TransactionBo> {

	    @Autowired
	    private ElasticSearchOperations2 elasticSearchTemplate;

	    @Autowired
	    private AgentStatElasticSearchOperationFactory agentStatElasticSearchOperationFactory;

	    @Autowired
	    private TransactionSerializer transactionSerializer;

	    @Override
	    public void insert(String agentId, List<TransactionBo> transactionBos) {
	        if (agentId == null) {
	            throw new NullPointerException("agentId must not be null");
	        }
	        if (transactionBos == null || transactionBos.isEmpty()) {
	            return;
	        }
	        List<Put> transactionPuts = this.agentStatElasticSearchOperationFactory.createPuts(agentId, AgentStatType.TRANSACTION, transactionBos, this.transactionSerializer);
	        if (!transactionPuts.isEmpty()) {
	            List<Put> rejectedPuts = this.elasticSearchTemplate.asyncPut(ElasticSearchTables.AGENT_STAT_VER2, transactionPuts);
	            if (CollectionUtils.isNotEmpty(rejectedPuts)) {
	                this.elasticSearchTemplate.put(ElasticSearchTables.AGENT_STAT_VER2, rejectedPuts);
	            }
	        }
	    }
	}
