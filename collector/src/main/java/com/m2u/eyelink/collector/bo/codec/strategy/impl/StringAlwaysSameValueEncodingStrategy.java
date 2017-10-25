package com.m2u.eyelink.collector.bo.codec.strategy.impl;

import java.util.ArrayList;
import java.util.List;

import com.m2u.eyelink.collector.bo.codec.StringTypedBufferHandler;
import com.m2u.eyelink.collector.bo.codec.strategy.EncodingStrategy;
import com.m2u.eyelink.util.Buffer;
import com.m2u.eyelink.util.CollectionUtils;

public class StringAlwaysSameValueEncodingStrategy implements EncodingStrategy<String> {

    private static final byte CODE = 12;

    private final StringTypedBufferHandler bufferHandler;

    public StringAlwaysSameValueEncodingStrategy(StringTypedBufferHandler bufferHandler) {
        this.bufferHandler = bufferHandler;
    }

    @Override
    public byte getCode() {
        return CODE;
    }

    @Override
    public void encodeValues(Buffer buffer, List<String> values) {
        if (CollectionUtils.isEmpty(values)) {
            throw new IllegalArgumentException("values may not be empty");
        }

        String initialValue = values.get(0);
        for (String value : values) {
            if (!isEquals(initialValue, value)) {
                throw new IllegalArgumentException("values must be all same value");
            }
        }

        bufferHandler.put(buffer, initialValue);
    }

    public boolean isEquals(String string1, String string2) {
        if (string1 == null) {
            return string2 == null;
        }

        return string1.equals(string2);
    }

    @Override
    public List<String> decodeValues(Buffer buffer, int numValues) {
        List<String> values = new ArrayList<String>(numValues);

        String value = bufferHandler.read(buffer);
        for (int i = 0; i < numValues; i++) {
            values.add(value);
        }

        return values;
    }

}
