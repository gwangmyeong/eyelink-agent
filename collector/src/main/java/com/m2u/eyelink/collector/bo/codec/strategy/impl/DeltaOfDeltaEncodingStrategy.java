package com.m2u.eyelink.collector.bo.codec.strategy.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.m2u.eyelink.collector.bo.codec.ArithmeticOperation;
import com.m2u.eyelink.collector.bo.codec.TypedBufferHandler;
import com.m2u.eyelink.collector.bo.codec.strategy.EncodingStrategy;
import com.m2u.eyelink.util.Buffer;

public abstract class DeltaOfDeltaEncodingStrategy<T extends Number> implements EncodingStrategy<T> {

    private static final byte CODE = 3;

    protected final TypedBufferHandler<T> bufferHandler;
    protected final ArithmeticOperation<T> operation;

    protected DeltaOfDeltaEncodingStrategy(TypedBufferHandler<T> bufferHandler, ArithmeticOperation<T> operation) {
        this.bufferHandler = bufferHandler;
        this.operation = operation;
    }

    @Override
    public byte getCode() {
        return CODE;
    }

    public static class Unsigned<T extends Number> extends DeltaOfDeltaEncodingStrategy<T> {

        public Unsigned(TypedBufferHandler<T> bufferHandler, ArithmeticOperation<T> operation) {
            super(bufferHandler, operation);
        }

        @Override
        public void encodeValues(Buffer buffer, List<T> values) {
            if (values.isEmpty()) {
                return;
            }
            T initialValue = values.get(0);
            this.bufferHandler.putV(buffer, initialValue);
            T previousValue = initialValue;
            T previousDelta = this.operation.zero();
            // skip first value as this value is stored without compression
            for (int i = 1; i < values.size(); ++i) {
                T value = values.get(i);
                T delta = this.operation.diff(value, previousValue);
                this.bufferHandler.putSV(buffer, this.operation.diff(delta, previousDelta));
                previousValue = value;
                previousDelta = delta;
            }
        }

        @Override
        public List<T> decodeValues(Buffer buffer, int numValues) {
            if (numValues < 1) {
                return Collections.emptyList();
            }
            List<T> values = new ArrayList<T>(numValues);
            T initialValue = this.bufferHandler.readV(buffer);
            values.add(initialValue);
            T previousValue = initialValue;
            T previousDelta = this.operation.zero();
            // loop through numValues - 1 as the first value is simply read from buffer
            for (int i = 0; i < numValues - 1; ++i) {
                T delta = this.operation.add(previousDelta, this.bufferHandler.readSV(buffer));
                T value = this.operation.add(previousValue, delta);
                values.add(value);
                previousValue = value;
                previousDelta = delta;
            }
            return values;
        }
    }
}
