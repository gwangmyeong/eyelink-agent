package com.m2u.eyelink.collector.mapper.thrift.stat;

import org.springframework.stereotype.Component;

import com.m2u.eyelink.collector.bo.stat.DataSourceBo;
import com.m2u.eyelink.collector.mapper.thrift.ThriftBoMapper;
import com.m2u.eyelink.thrift.TDataSource;

@Component
public class DataSourceBoMapper implements ThriftBoMapper<DataSourceBo, TDataSource> {

    @Override
    public DataSourceBo map(TDataSource dataSource) {
        DataSourceBo dataSourceBo = new DataSourceBo();
        dataSourceBo.setId(dataSource.getId());
        dataSourceBo.setServiceTypeCode(dataSource.getServiceTypeCode());
        dataSourceBo.setDatabaseName(dataSource.getDatabaseName());
        dataSourceBo.setJdbcUrl(dataSource.getUrl());
        dataSourceBo.setActiveConnectionSize(dataSource.getActiveConnectionSize());
        dataSourceBo.setMaxConnectionSize(dataSource.getMaxConnectionSize());
        return dataSourceBo;
    }

}
