package com.m2u.eyelink.collector.dao.elasticsearch.stat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.m2u.eyelink.collector.alarm.AgentAlarmType;
import com.m2u.eyelink.collector.bo.serializer.stat.AgentStatElasticSearchOperationFactory;
import com.m2u.eyelink.collector.bo.serializer.stat.JvmGcSerializer;
import com.m2u.eyelink.collector.bo.stat.AgentStatDataPoint;
import com.m2u.eyelink.collector.bo.stat.AgentStatType;
import com.m2u.eyelink.collector.bo.stat.JvmGcBo;
import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchOperations2;
import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchTables;
import com.m2u.eyelink.collector.common.elasticsearch.Put;
import com.m2u.eyelink.collector.dao.AgentStatDaoV2;
import com.m2u.eyelink.collector.handler.AgentStatHandlerV2;
import com.m2u.eyelink.collector.util.ElasticSearchUtils;
import com.m2u.eyelink.util.CollectionUtils;

@Repository
public class ElasticSearchJvmGcDao implements AgentStatDaoV2<JvmGcBo> {

	private final Logger logger = LoggerFactory.getLogger(AgentStatHandlerV2.class.getName());
	
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
        
        List<Map<String, Object>> listJvmGcBo = this.agentStatElasticSearchOperationFactory.createList(jvmGcBos);
        if (!listJvmGcBo.isEmpty()) {
            boolean isSuccess = this.elasticSearchTemplate.asyncPut(ElasticSearchUtils.generateIndexName(agentId), ElasticSearchTables.TYPE_AGENT_STAT_JVMGC, listJvmGcBo);
            if (!isSuccess) {
                this.elasticSearchTemplate.put(ElasticSearchUtils.generateIndexName(agentId), ElasticSearchTables.TYPE_AGENT_STAT_JVMGC, listJvmGcBo);
            }
        } else {
        		logger.info("listJvmGcBo is empty");
        }
        
        List<Map<String, Object>> listAlarm = createAlarmList(jvmGcBos);
        if (!listAlarm.isEmpty()) {
            boolean isSuccess = this.elasticSearchTemplate.asyncPut(ElasticSearchUtils.generateIndexName(agentId), ElasticSearchTables.TYPE_AGENT_ALARM, listAlarm);
            if (!isSuccess) {
                this.elasticSearchTemplate.put(ElasticSearchUtils.generateIndexName(agentId), ElasticSearchTables.TYPE_AGENT_ALARM, listAlarm);
            }
        } else {
        		logger.debug("listAlarm is empty");
        }

    }
    
    public List<Map<String, Object>> createAlarmList(List<JvmGcBo> jvmGcBos) {
    		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		
		Iterator<JvmGcBo> iterator = jvmGcBos.iterator();
		while (iterator.hasNext()) {
			AgentAlarmType agentAlarmType = null;
			JvmGcBo jvmGcBo = iterator.next();
			double memPer = Math.round(jvmGcBo.getHeapUsed() / jvmGcBo.getHeapMax() * 100) /100d;
			if (AgentAlarmType.MEMORY_100.getIsUse() && memPer == AgentAlarmType.MEMORY_100.getCode()) {
				agentAlarmType = AgentAlarmType.MEMORY_100;
			} else if (AgentAlarmType.MEMORY_90.getIsUse() && memPer > AgentAlarmType.MEMORY_90.getCode()) {
				agentAlarmType = AgentAlarmType.MEMORY_90;
			} else if (AgentAlarmType.MEMORY_80.getIsUse() && memPer > AgentAlarmType.MEMORY_80.getCode()) {
				agentAlarmType = AgentAlarmType.MEMORY_80;
			} else if (AgentAlarmType.MEMORY_70.getIsUse() && memPer > AgentAlarmType.MEMORY_70.getCode()) {
				agentAlarmType = AgentAlarmType.MEMORY_70;
			} else {
				continue;
			}
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("agentId", jvmGcBo.getAgentId());
			map.put("alarmType", agentAlarmType.getCode());
			map.put("alarmTypeName", agentAlarmType.getDesc());
			map.put("startTimestamp", jvmGcBo.getTimestamp());
			map.put("jvmGcBo", this.agentStatElasticSearchOperationFactory.converBOToMap(jvmGcBo));
			list.add(map);
	    }
		return list;
    }
}
