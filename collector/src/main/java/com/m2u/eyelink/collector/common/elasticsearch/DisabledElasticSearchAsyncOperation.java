package com.m2u.eyelink.collector.common.elasticsearch;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DisabledElasticSearchAsyncOperation implements ElasticSearchAsyncOperation {

	static final DisabledElasticSearchAsyncOperation INSTANCE = new DisabledElasticSearchAsyncOperation();

	@Override
	public boolean isAvailable() {
		return false;
	}

	@Override
	public boolean put(TableName tableName, Put put) {
		return false;
	}

	@Override
	public List<Put> put(TableName tableName, List<Put> puts) {
		return puts;
	}

	@Override
	public Long getOpsCount() {
		return -1L;
	}

	@Override
	public Long getOpsRejectedCount() {
		return -1L;
	}

	@Override
	public Long getCurrentOpsCount() {
		return -1L;
	}

	@Override
	public Long getOpsFailedCount() {
		return -1L;
	}

	@Override
	public Long getOpsAverageLatency() {
		return -1L;
	}

	@Override
	public Map<String, Long> getCurrentOpsCountForEachRegionServer() {
		return Collections.emptyMap();
	}

	@Override
	public Map<String, Long> getOpsFailedCountForEachRegionServer() {
		return Collections.emptyMap();
	}

	@Override
	public Map<String, Long> getOpsAverageLatencyForEachRegionServer() {
		return Collections.emptyMap();
	}

	@Override
	public boolean put(String indexName, String typeAgentStat, Map<String, Object> mapData) {
		// TODO Auto-generated method stub
		return false;
	}

}