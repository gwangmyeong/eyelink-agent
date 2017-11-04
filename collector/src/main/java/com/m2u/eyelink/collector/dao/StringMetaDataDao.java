package com.m2u.eyelink.collector.dao;

import java.util.List;

import com.m2u.eyelink.collector.bo.StringMetaDataBo;
import com.m2u.eyelink.thrift.dto.TStringMetaData;

public interface StringMetaDataDao {

    void insert(TStringMetaData stringMetaData);

	List<StringMetaDataBo> getStringMetaData(String agentId, long agentStartTime, int stringMetaDataId);
}
