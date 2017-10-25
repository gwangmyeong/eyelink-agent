package com.m2u.eyelink.collector.dao;

public interface HostApplicationMapDao {
    void insert(String host, String bindApplicationName, short bindServiceType, String parentApplicationName, short parentServiceType);
}
