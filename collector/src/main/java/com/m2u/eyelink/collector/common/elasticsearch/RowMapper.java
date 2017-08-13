package com.m2u.eyelink.collector.common.elasticsearch;

public interface RowMapper<T> {

    T mapRow(Result result, int rowNum) throws Exception;
}
