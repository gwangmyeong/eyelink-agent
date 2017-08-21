package com.m2u.eyelink.collector.bo.codec.strategy;

import java.util.List;

import com.m2u.eyelink.util.Buffer;

public interface EncodingStrategy<T> {
    byte getCode();
    void encodeValues(Buffer buffer, List<T> values);
    List<T> decodeValues(Buffer buffer, int numValues);

}
