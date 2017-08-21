package com.m2u.eyelink.collector.bo.codec.strategy.impl;

import java.util.ArrayList;
import java.util.List;

import com.m2u.eyelink.collector.bo.codec.TypedBufferHandler;
import com.m2u.eyelink.collector.bo.codec.strategy.EncodingStrategy;
import com.m2u.eyelink.util.Buffer;

public abstract class RepeatCountEncodingStrategy<T extends Number> implements EncodingStrategy<T> {

    private static final byte CODE = 1;

    protected final TypedBufferHandler<T> bufferHandler;

    protected RepeatCountEncodingStrategy(TypedBufferHandler<T> bufferHandler) {
        this.bufferHandler = bufferHandler;
    }

    @Override
    public byte getCode() {
        return CODE;
    }

    public static class Unsigned<T extends Number> extends RepeatCountEncodingStrategy<T> {

        public Unsigned(TypedBufferHandler<T> bufferHandler) {
            super(bufferHandler);
        }

        @Override
        public void encodeValues(Buffer buffer, List<T> values) {
            T previousValue = null;
            int count = 0;
            for (T value : values) {
                if (!value.equals(previousValue)) {
                    if (previousValue != null) {
                        buffer.putVInt(count);
                        this.bufferHandler.putV(buffer, previousValue);
                    }
                    previousValue = value;
                    count = 1;
                } else {
                    count++;
                }
            }
            if (count > 0) {
                buffer.putVInt(count);
                this.bufferHandler.putV(buffer, previousValue);
            }
        }

        @Override
        public List<T> decodeValues(Buffer buffer, int numValues) {
            List<T> values = new ArrayList<T>(numValues);
            int totalCount = 0;
            while (totalCount < numValues) {
                int count = buffer.readVInt();
                T value = this.bufferHandler.readV(buffer);
                for (int i = 0; i < count; ++i) {
                    values.add(value);
                    totalCount++;
                }
            }
            return values;
        }
    }
}
