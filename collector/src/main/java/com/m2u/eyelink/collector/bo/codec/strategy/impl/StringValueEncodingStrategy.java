package com.m2u.eyelink.collector.bo.codec.strategy.impl;

import java.util.ArrayList;
import java.util.List;

import com.m2u.eyelink.collector.bo.codec.StringTypedBufferHandler;
import com.m2u.eyelink.collector.bo.codec.strategy.EncodingStrategy;
import com.m2u.eyelink.util.Buffer;

public class StringValueEncodingStrategy implements EncodingStrategy<String> {

    private static final byte CODE = 10;

    private final StringTypedBufferHandler bufferHandler;

    public StringValueEncodingStrategy(StringTypedBufferHandler bufferHandler) {
        this.bufferHandler = bufferHandler;
    }

    @Override
    public byte getCode() {
        return CODE;
    }

    @Override
    public void encodeValues(Buffer buffer, List<String> values) {
        for (String value : values) {
            bufferHandler.put(buffer, value);
        }
    }

    @Override
    public List<String> decodeValues(Buffer buffer, int numValues) {
        List<String> values = new ArrayList<String>(numValues);
        for (int i = 0; i < numValues; ++i) {
            values.add(this.bufferHandler.read(buffer));
        }
        return values;
    }

}
