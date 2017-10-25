package com.m2u.eyelink.collector.dao.elasticsearch.statistics;

public interface ColumnName {
    byte[] getColumnName();

    long getCallCount();

    void setCallCount(long callCount);
}
