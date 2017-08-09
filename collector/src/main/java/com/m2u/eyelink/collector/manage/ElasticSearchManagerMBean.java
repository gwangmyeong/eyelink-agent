package com.m2u.eyelink.collector.manage;

import java.util.Map;

public interface ElasticSearchManagerMBean {

    Long getAsyncOpsCount();

    Long getAsyncOpsRejectedCount();

    Map<String, Long> getCurrentAsyncOpsCountForEachRegionServer();

    Map<String, Long> getAsyncOpsFailedCountForEachRegionServer();

    Map<String, Long> getAsyncOpsAverageLatencyForEachRegionServer();

}
