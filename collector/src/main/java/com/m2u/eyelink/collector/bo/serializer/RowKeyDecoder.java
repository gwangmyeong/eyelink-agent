package com.m2u.eyelink.collector.bo.serializer;

public interface RowKeyDecoder<V> {

    V decodeRowKey(byte[] rowkey);

}
