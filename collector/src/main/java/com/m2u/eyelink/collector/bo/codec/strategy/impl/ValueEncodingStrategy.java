package com.m2u.eyelink.collector.bo.codec.strategy.impl;

import java.util.ArrayList;
import java.util.List;

import com.m2u.eyelink.collector.bo.codec.TypedBufferHandler;
import com.m2u.eyelink.collector.bo.codec.strategy.EncodingStrategy;
import com.m2u.eyelink.util.Buffer;

public abstract class ValueEncodingStrategy<T extends Number> implements EncodingStrategy<T> {

    private static final byte CODE = 0;

    protected final TypedBufferHandler<T> bufferHandler;

    protected ValueEncodingStrategy(TypedBufferHandler<T> bufferHandler) {
        this.bufferHandler = bufferHandler;
    }

    @Override
    public byte getCode() {
        return CODE;
    }

    public static class Unsigned<T extends Number> extends ValueEncodingStrategy<T> {

        public Unsigned(TypedBufferHandler<T> bufferHandler) {
            super(bufferHandler);
        }

        @Override
        public void encodeValues(Buffer buffer, List<T> values) {
            for (T value : values) {
                this.bufferHandler.putV(buffer, value);
            }
        }

        @Override
        public List<T> decodeValues(Buffer buffer, int numValues) {
            List<T> values = new ArrayList<T>(numValues);
            for (int i = 0; i < numValues; ++i) {
                values.add(this.bufferHandler.readV(buffer));
            }
            return values;
        }
    }
}
