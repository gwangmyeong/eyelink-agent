package com.m2u.eyelink.collector.common.elasticsearch;

import java.util.List;
import java.util.Map;

public interface ElasticSearchAsyncOperation {

    boolean isAvailable();

    boolean put(TableName tableName, final Put put);

    List<Put> put(TableName tableName, final List<Put> puts);

    Long getOpsCount();

    Long getOpsRejectedCount();

    
    Long getCurrentOpsCount();

    Long getOpsFailedCount();

    Long getOpsAverageLatency();

    Map<String, Long> getCurrentOpsCountForEachRegionServer();

    Map<String, Long> getOpsFailedCountForEachRegionServer();

    Map<String, Long> getOpsAverageLatencyForEachRegionServer();

    boolean put(String indexName, String typeAgentStat, Map<String, Object> mapData);
    
}