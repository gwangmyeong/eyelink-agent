package com.m2u.eyelink.collector.util;

public interface ObjectPoolFactory<T> {
    T create();

    void beforeReturn(T t);
}
