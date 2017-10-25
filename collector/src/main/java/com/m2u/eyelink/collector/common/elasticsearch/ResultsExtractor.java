package com.m2u.eyelink.collector.common.elasticsearch;

public interface ResultsExtractor<T> {
	T extractData(ResultScanner results) throws Exception;
}
