package com.m2u.eyelink.collector.bo.codec.strategy.impl;

import java.util.ArrayList;
import java.util.List;

import com.m2u.eyelink.collector.bo.codec.StringTypedBufferHandler;
import com.m2u.eyelink.collector.bo.codec.strategy.EncodingStrategy;
import com.m2u.eyelink.util.Buffer;

public class StringRepeatCountEncodingStrategy implements EncodingStrategy<String> {

    private static final byte CODE = 11;

    private final StringTypedBufferHandler bufferHandler;

    public StringRepeatCountEncodingStrategy(StringTypedBufferHandler bufferHandler) {
        this.bufferHandler = bufferHandler;
    }

    @Override
    public byte getCode() {
        return CODE;
    }

    @Override
    public void encodeValues(Buffer buffer, List<String> values) {
        String previousValue = null;
        int count = 0;
        for (String value : values) {
            if (!value.equals(previousValue)) {
                if (previousValue != null) {
                    buffer.putVInt(count);
                    this.bufferHandler.put(buffer, previousValue);
                }
                previousValue = value;
                count = 1;
            } else {
                count++;
            }
        }
        if (count > 0) {
            buffer.putVInt(count);
            this.bufferHandler.put(buffer, previousValue);
        }
    }

    @Override
    public List<String> decodeValues(Buffer buffer, int numValues) {
        List<String> values = new ArrayList<String>(numValues);
        int totalCount = 0;
        while (totalCount < numValues) {
            int count = buffer.readVInt();
            String value = this.bufferHandler.read(buffer);
            for (int i = 0; i < count; ++i) {
                values.add(value);
                totalCount++;
            }
        }
        return values;
    }

}
