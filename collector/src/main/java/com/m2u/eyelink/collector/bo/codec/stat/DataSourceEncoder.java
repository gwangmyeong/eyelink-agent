package com.m2u.eyelink.collector.bo.codec.stat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.m2u.eyelink.collector.bo.stat.DataSourceListBo;

@Component
public class DataSourceEncoder extends AgentStatEncoder<DataSourceListBo> {

    @Autowired
    private DataSourceEncoder(@Qualifier("dataSourceCodecV2") AgentStatCodec<DataSourceListBo> dataSourceListCodec) {
        super(dataSourceListCodec);
    }

}