package com.m2u.eyelink.collector.bo.serializer.stat;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.m2u.eyelink.collector.bo.serializer.ElasticSearchSerializer;
import com.m2u.eyelink.collector.bo.stat.AgentStatDataPoint;
import com.m2u.eyelink.collector.bo.stat.AgentStatType;
import com.m2u.eyelink.collector.common.elasticsearch.AbstractRowKeyDistributor;
import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchTables;
import com.m2u.eyelink.collector.common.elasticsearch.Put;
import com.m2u.eyelink.collector.common.elasticsearch.Scan;

@Component
public class AgentStatElasticSearchOperationFactory {

	private final AgentStatRowKeyEncoder rowKeyEncoder;

	private final AgentStatRowKeyDecoder rowKeyDecoder;

	private final AbstractRowKeyDistributor rowKeyDistributor;

	@Autowired
    public AgentStatElasticSearchOperationFactory(
            AgentStatRowKeyEncoder rowKeyEncoder,
            AgentStatRowKeyDecoder rowKeyDecoder,
            @Qualifier("agentStatV2RowKeyDistributor") AbstractRowKeyDistributor rowKeyDistributor) {
        Assert.notNull(rowKeyEncoder, "rowKeyEncoder must not be null");
        Assert.notNull(rowKeyDecoder, "rowKeyDecoder must not be null");
        Assert.notNull(rowKeyDistributor, "rowKeyDistributor must not be null");
        this.rowKeyEncoder = rowKeyEncoder;
        this.rowKeyDecoder = rowKeyDecoder;
        this.rowKeyDistributor = rowKeyDistributor;
    }

	public <T extends AgentStatDataPoint> List<Put> createPuts(String agentId, AgentStatType agentStatType,
			List<T> agentStatDataPoints, ElasticSearchSerializer<List<T>, Put> agentStatSerializer) {
		if (agentStatDataPoints == null || agentStatDataPoints.isEmpty()) {
			return Collections.emptyList();
		}
		Map<Long, List<T>> timeslots = slotAgentStatDataPoints(agentStatDataPoints);
		List<Put> puts = new ArrayList<Put>();
		for (Map.Entry<Long, List<T>> timeslot : timeslots.entrySet()) {
			long baseTimestamp = timeslot.getKey();
			List<T> slottedAgentStatDataPoints = timeslot.getValue();

			final AgentStatRowKeyComponent rowKeyComponent = new AgentStatRowKeyComponent(agentId, agentStatType,
					baseTimestamp);
			byte[] rowKey = this.rowKeyEncoder.encodeRowKey(rowKeyComponent);
			byte[] distributedRowKey = this.rowKeyDistributor.getDistributedKey(rowKey);

			Put put = new Put(distributedRowKey);
			agentStatSerializer.serialize(slottedAgentStatDataPoints, put, null);
			puts.add(put);
		}
		return puts;
	}

	// create by bsh
	public <T extends AgentStatDataPoint> List<Map<String, Object>> createList(List<T> agentStatDataPoints)  {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		
		Iterator<T> iterator = agentStatDataPoints.iterator();
		while (iterator.hasNext()) {
			@SuppressWarnings("unchecked")
//			Map<String, Object> map = (Map<String, Object>) iterator.next();
			Map<String, Object> map1 = ConverBOToMap(iterator.next());
			list.add(map1);
	    }
		return list;
	}
	
	public Map<String, Object> ConverBOToMap(Object obj) {
		try {
			// Field[] fields = obj.getClass().getFields();
			// private field는 나오지 않음.
			Field[] fields = obj.getClass().getDeclaredFields();
			Map<String, Object> resultMap = new HashMap<String, Object>();
			for (int i = 0; i <= fields.length - 1; i++) {
				fields[i].setAccessible(true);
				resultMap.put(fields[i].getName(), fields[i].get(obj));
			}
			return resultMap;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Scan createScan(String agentId, AgentStatType agentStatType, long startTimestamp, long endTimestamp) {
		final AgentStatRowKeyComponent startRowKeyComponent = new AgentStatRowKeyComponent(agentId, agentStatType,
				AgentStatUtils.getBaseTimestamp(endTimestamp));
		final AgentStatRowKeyComponent endRowKeyComponenet = new AgentStatRowKeyComponent(agentId, agentStatType,
				AgentStatUtils.getBaseTimestamp(startTimestamp) - ElasticSearchTables.AGENT_STAT_TIMESPAN_MS);
		byte[] startRowKey = this.rowKeyEncoder.encodeRowKey(startRowKeyComponent);
		byte[] endRowKey = this.rowKeyEncoder.encodeRowKey(endRowKeyComponenet);
		return new Scan(startRowKey, endRowKey);
	}

	public AbstractRowKeyDistributor getRowKeyDistributor() {
		return this.rowKeyDistributor;
	}

	public String getAgentId(byte[] distributedRowKey) {
		byte[] originalRowKey = this.rowKeyDistributor.getOriginalKey(distributedRowKey);
		return this.rowKeyDecoder.decodeRowKey(originalRowKey).getAgentId();
	}

	public AgentStatType getAgentStatType(byte[] distributedRowKey) {
		byte[] originalRowKey = this.rowKeyDistributor.getOriginalKey(distributedRowKey);
		return this.rowKeyDecoder.decodeRowKey(originalRowKey).getAgentStatType();
	}

	public long getBaseTimestamp(byte[] distributedRowKey) {
		byte[] originalRowKey = this.rowKeyDistributor.getOriginalKey(distributedRowKey);
		return this.rowKeyDecoder.decodeRowKey(originalRowKey).getBaseTimestamp();
	}

	private <T extends AgentStatDataPoint> Map<Long, List<T>> slotAgentStatDataPoints(List<T> agentStatDataPoints) {
		Map<Long, List<T>> timeslots = new TreeMap<Long, List<T>>();
		for (T agentStatDataPoint : agentStatDataPoints) {
			long timestamp = agentStatDataPoint.getTimestamp();
			long timeslot = AgentStatUtils.getBaseTimestamp(timestamp);
			List<T> slottedDataPoints = timeslots.get(timeslot);
			if (slottedDataPoints == null) {
				slottedDataPoints = new ArrayList<T>();
				timeslots.put(timeslot, slottedDataPoints);
			}
			slottedDataPoints.add(agentStatDataPoint);
		}
		return timeslots;
	}
}
