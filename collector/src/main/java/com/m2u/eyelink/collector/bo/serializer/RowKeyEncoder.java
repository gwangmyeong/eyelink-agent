package com.m2u.eyelink.collector.bo.serializer;

public interface RowKeyEncoder<V> {

    byte[] encodeRowKey(V value);

}
