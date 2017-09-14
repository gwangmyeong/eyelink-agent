package com.m2u.eyelink.collector.common.elasticsearch;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.collector.bo.serializer.stat.AgentStatElasticSearchOperationFactory;
import com.m2u.eyelink.collector.bo.stat.CpuLoadBo;
import com.m2u.eyelink.collector.bo.stat.JvmGcBo;
import com.m2u.eyelink.collector.dao.elasticsearch.ElasticSearchAgentAlarmDao;
import com.m2u.eyelink.collector.mapper.thrift.stat.AgentStatBo;

@RunWith(MockitoJUnitRunner.class)
public class ElasticSearchAgentAlarmDaoTest {
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @InjectMocks
    private ElasticSearchAgentAlarmDao esAgentAlarmDao;

    @Mock
    private AgentStatElasticSearchOperationFactory agentStatElasticSearchOperationFactory;
    
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }
    
	@Test
	public void testRound() {
		JvmGcBo jvmGcBo = new JvmGcBo();
		jvmGcBo.setHeapUsed(448);
		jvmGcBo.setHeapMax(1908);
		double memPer = Math.round((double)jvmGcBo.getHeapUsed() / jvmGcBo.getHeapMax() * 100 * 100) / 100.0;
		assertTrue(memPer > 10);
		
		CpuLoadBo cpuLoadBo = new CpuLoadBo();
		cpuLoadBo.setJvmCpuLoad(9.87102);
		double cpuPer = Math.round(cpuLoadBo.getJvmCpuLoad()* 100) / 100.0;
		assertEquals(cpuPer, 9.87, 0.0);
	}
	
	@Test
	public void createAlarmListTest() {
		AgentStatBo agentStatBo = new AgentStatBo();
		
		List<JvmGcBo> jvmGcBos = new ArrayList<JvmGcBo>();
		JvmGcBo jvmGcBo = new JvmGcBo();
		jvmGcBo.setHeapUsed(448);
		jvmGcBo.setHeapMax(1908);
		jvmGcBos.add(jvmGcBo);
		JvmGcBo jvmGcBo1 = new JvmGcBo();
		jvmGcBo1.setHeapUsed(1900);
		jvmGcBo1.setHeapMax(1908);
		jvmGcBos.add(jvmGcBo1);
		agentStatBo.setJvmGcBos(jvmGcBos);

		List<CpuLoadBo> cpuLoadBos = new ArrayList<CpuLoadBo>();
		CpuLoadBo cpuLoadBo = new CpuLoadBo();
		cpuLoadBo.setJvmCpuLoad(9.87102);
		cpuLoadBos.add(cpuLoadBo);
		CpuLoadBo cpuLoadBo1 = new CpuLoadBo();
		cpuLoadBo1.setJvmCpuLoad(79.87102);
		cpuLoadBos.add(cpuLoadBo1);
		agentStatBo.setCpuLoadBos(cpuLoadBos);
		
		// TODO need to test mokito logic 
		when(agentStatElasticSearchOperationFactory.converBOToMap(jvmGcBo)).thenReturn(getJvmGcBo());
		
		List<Map<String, Object>> listAlarm = esAgentAlarmDao.createAlarmList(agentStatBo);
		if (!listAlarm.isEmpty()) {
			logger.debug(listAlarm.toString());
		} else {
			logger.debug("listAlarm is empty");
			fail("listAlarm is Empty");
		}
	}
	
	public Map<String, Object> getJvmGcBo() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("heapUsed", "90");
		return map;
	}
}
