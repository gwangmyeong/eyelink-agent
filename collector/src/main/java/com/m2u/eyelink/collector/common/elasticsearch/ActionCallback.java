package com.m2u.eyelink.collector.common.elasticsearch;

public interface ActionCallback<T> {
    T doActionGet() throws Throwable;
}
