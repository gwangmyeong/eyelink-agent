package com.m2u.eyelink.collector.bo.serializer.stat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.m2u.eyelink.collector.bo.codec.stat.DataSourceEncoder;
import com.m2u.eyelink.collector.bo.stat.DataSourceListBo;

@Component
public class DataSourceSerializer extends AgentStatSerializer<DataSourceListBo> {

    @Autowired
    public DataSourceSerializer(DataSourceEncoder dataSourceEncoder) {
        super(dataSourceEncoder);
    }

}