package com.m2u.eyelink.collector.common.elasticsearch;

public interface ValueMapper<T> {
	byte[] mapValue(T value);
}
