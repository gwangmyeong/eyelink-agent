package com.m2u.eyelink.collector.dao.elasticsearch;

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
import com.m2u.eyelink.collector.bo.stat.CpuLoadBo;
import com.m2u.eyelink.collector.bo.stat.JvmGcBo;
import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchOperations2;
import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchTables;
import com.m2u.eyelink.collector.dao.AgentAlarmDao;
import com.m2u.eyelink.collector.mapper.thrift.stat.AgentStatBo;
import com.m2u.eyelink.collector.util.ElasticSearchUtils;
import com.m2u.eyelink.collector.util.TimeUtils;

@Repository
public class ElasticSearchAgentAlarmDao implements AgentAlarmDao {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ElasticSearchOperations2 elasticSearchTemplate;

    @Autowired
    private AgentStatElasticSearchOperationFactory agentStatElasticSearchOperationFactory;

	@Override
	public void insert(String agentId, AgentStatBo agentStatBo) {
		if (agentId == null) {
			throw new NullPointerException("agentId must not be null");
		}
//		if (logger.isDebugEnabled()) {
//			logger.debug("insert:{}", agentStatBo);
//		}

		List<Map<String, Object>> listAlarm = createAlarmList(agentStatBo);
		if (!listAlarm.isEmpty()) {
			boolean isSuccess = this.elasticSearchTemplate.asyncPut(ElasticSearchUtils.generateIndexName(agentId),
					ElasticSearchTables.TYPE_AGENT_ALARM, listAlarm);
			if (!isSuccess) {
				this.elasticSearchTemplate.put(ElasticSearchUtils.generateIndexName(agentId),
						ElasticSearchTables.TYPE_AGENT_ALARM, listAlarm);
			}
		} else {
			logger.debug("listAlarm is empty");
		}

	}

	public List<Map<String, Object>> createAlarmList(AgentStatBo agentStatBo) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		boolean isTF, isTF2 = false;
		List<JvmGcBo> jvmGcBos = agentStatBo.getJvmGcBos();
		List<CpuLoadBo> cpuLoadBos = agentStatBo.getCpuLoadBos();
		
		Iterator<JvmGcBo> iterator = jvmGcBos.iterator();
		Iterator<CpuLoadBo> iterator2 = cpuLoadBos.iterator();
		while (iterator.hasNext()) {
			isTF = false;
			isTF2 = false;
			AgentAlarmType agentAlarmType = null;
			JvmGcBo jvmGcBo = iterator.next();
			double memPer = Math.round((double)jvmGcBo.getHeapUsed() / jvmGcBo.getHeapMax() * 100 * 100) / 100.0;
			if (AgentAlarmType.MEMORY_100.getIsUse() && memPer == AgentAlarmType.MEMORY_100.getCode()) {
				agentAlarmType = AgentAlarmType.MEMORY_100;
				isTF = true;
			} else if (AgentAlarmType.MEMORY_90.getIsUse() && memPer > AgentAlarmType.MEMORY_90.getCode()) {
				agentAlarmType = AgentAlarmType.MEMORY_90;
				isTF = true;
			} else if (AgentAlarmType.MEMORY_80.getIsUse() && memPer > AgentAlarmType.MEMORY_80.getCode()) {
				agentAlarmType = AgentAlarmType.MEMORY_80;
				isTF = true;
			} else if (AgentAlarmType.MEMORY_70.getIsUse() && memPer > AgentAlarmType.MEMORY_70.getCode()) {
				agentAlarmType = AgentAlarmType.MEMORY_70;
				isTF = true;
			} 
			AgentAlarmType agentAlarmType2 = null;
			CpuLoadBo cpuLoadBo = iterator2.next();
			double cpuPer = Math.round((double)cpuLoadBo.getJvmCpuLoad() * 100) / 100.0;
			if (AgentAlarmType.CPU_100.getIsUse() && cpuPer == AgentAlarmType.CPU_100.getCode()) {
				agentAlarmType2 = AgentAlarmType.CPU_100;
				isTF2 = true;
			} else if (AgentAlarmType.CPU_90.getIsUse() && cpuPer > AgentAlarmType.CPU_90.getCode()) {
				agentAlarmType2 = AgentAlarmType.CPU_90;
				isTF2 = true;
			} else if (AgentAlarmType.CPU_80.getIsUse() && cpuPer > AgentAlarmType.CPU_80.getCode()) {
				agentAlarmType2 = AgentAlarmType.CPU_80;
				isTF2 = true;
			} else if (AgentAlarmType.CPU_70.getIsUse() && cpuPer > AgentAlarmType.CPU_70.getCode()) {
				agentAlarmType2 = AgentAlarmType.CPU_70;
				isTF2 = true;
			} 

			if (isTF || isTF2) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("agentId", jvmGcBo.getAgentId());
				// startTimestamp, timestamp in jvmGcBo, CpuLoadBo is same
				map.put("startTimestamp", TimeUtils.convertEpochToDate(jvmGcBo.getStartTimestamp()));
				map.put("timestamp", TimeUtils.convertEpochToDate(jvmGcBo.getTimestamp()));
				if (isTF && isTF2) {
					map.put("alarmType", agentAlarmType.name() + ", " + agentAlarmType2.name());
					map.put("alarmTypeName", agentAlarmType.getDesc() + ", " + agentAlarmType2.getDesc());
					map.put("jvmGcBo", this.agentStatElasticSearchOperationFactory.converBOToMap(jvmGcBo));
					map.put("cpuLoadBo", this.agentStatElasticSearchOperationFactory.converBOToMap(cpuLoadBo));
				} else if (isTF) {
					map.put("alarmType",agentAlarmType.name());
					map.put("alarmTypeName", agentAlarmType.getDesc());
					map.put("jvmGcBo", this.agentStatElasticSearchOperationFactory.converBOToMap(jvmGcBo));
				} else if (isTF2) {
					map.put("alarmType", agentAlarmType2.name());
					map.put("alarmTypeName", agentAlarmType2.getDesc());
					map.put("cpuLoadBo", this.agentStatElasticSearchOperationFactory.converBOToMap(cpuLoadBo));
				}
				list.add(map);
			}
		}
		return list;
	}
}
