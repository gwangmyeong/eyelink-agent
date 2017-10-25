package com.m2u.eyelink.collector.manage;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchAsyncOperation;

public class ElasticSearchManager  extends AbstractCollectorManager implements ElasticSearchManagerMBean {

	    @Autowired
	    private ElasticSearchAsyncOperation elasticSearchAsyncOperation;

	    @Override
	    public Long getAsyncOpsCount() {
	        return elasticSearchAsyncOperation.getOpsCount();
	    }

	    @Override
	    public Long getAsyncOpsRejectedCount() {
	        return elasticSearchAsyncOperation.getOpsRejectedCount();
	    }

	    @Override
	    public Map<String, Long> getCurrentAsyncOpsCountForEachRegionServer() {
	        return elasticSearchAsyncOperation.getCurrentOpsCountForEachRegionServer();
	    }

	    @Override
	    public Map<String, Long> getAsyncOpsFailedCountForEachRegionServer() {
	        return elasticSearchAsyncOperation.getOpsFailedCountForEachRegionServer();
	    }

	    @Override
	    public Map<String, Long> getAsyncOpsAverageLatencyForEachRegionServer() {
	        return elasticSearchAsyncOperation.getOpsAverageLatencyForEachRegionServer();
	    }

	}