package com.m2u.eyelink.collector.util;

public interface PooledObject<T> {
    T getObject();

    void returnObject();

}
