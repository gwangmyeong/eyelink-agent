package com.m2u.eyelink.collector.common.elasticsearch;

import org.elasticsearch.action.search.SearchResponse;

public interface RowMapper<T> {

	// FIXME, need to delete by bsh
    T mapRow(Result result, int rowNum) throws Exception;

	T mapRow(SearchResponse sres, int rowNum) throws Exception;
}
