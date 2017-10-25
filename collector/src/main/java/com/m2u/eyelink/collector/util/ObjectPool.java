package com.m2u.eyelink.collector.util;

public interface ObjectPool<T> {
	PooledObject<T> getObject();
}
