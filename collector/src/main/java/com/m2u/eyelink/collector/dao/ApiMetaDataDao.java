package com.m2u.eyelink.collector.dao;

import java.util.List;

import com.m2u.eyelink.collector.bo.ApiMetaDataBo;
import com.m2u.eyelink.context.TApiMetaData;

public interface ApiMetaDataDao {
	void insert(TApiMetaData apiMetaData);
	
    List<ApiMetaDataBo> getApiMetaData(String agentId, long time, int apiId);
}