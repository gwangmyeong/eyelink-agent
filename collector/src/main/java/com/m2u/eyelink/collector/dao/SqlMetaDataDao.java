package com.m2u.eyelink.collector.dao;

import java.util.List;

import com.m2u.eyelink.collector.bo.SqlMetaDataBo;
import com.m2u.eyelink.thrift.dto.TSqlMetaData;

public interface SqlMetaDataDao {
	void insert(TSqlMetaData sqlMetaData);
	
    List<SqlMetaDataBo> getSqlMetaData(String agentId, long time, int sqlId);
}
