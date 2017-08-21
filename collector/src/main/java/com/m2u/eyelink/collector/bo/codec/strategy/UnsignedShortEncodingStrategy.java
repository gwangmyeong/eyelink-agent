package com.m2u.eyelink.collector.bo.codec.strategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.m2u.eyelink.collector.bo.codec.TypedBufferHandler;
import com.m2u.eyelink.collector.bo.codec.strategy.impl.RepeatCountEncodingStrategy;
import com.m2u.eyelink.collector.bo.codec.strategy.impl.ValueEncodingStrategy;
import com.m2u.eyelink.util.Buffer;
import com.m2u.eyelink.util.BytesUtils;

public enum UnsignedShortEncodingStrategy implements EncodingStrategy<Short> {
    NONE(new ValueEncodingStrategy.Unsigned<Short>(TypedBufferHandler.SHORT_BUFFER_HANDLER)),
    REPEAT_COUNT(new RepeatCountEncodingStrategy.Unsigned<Short>(TypedBufferHandler.SHORT_BUFFER_HANDLER));

    private final EncodingStrategy<Short> delegate;

    UnsignedShortEncodingStrategy(EncodingStrategy<Short> delegate) {
        this.delegate = delegate;
    }

    @Override
    public byte getCode() {
        return this.delegate.getCode();
    }

    @Override
    public void encodeValues(Buffer buffer, List<Short> values) {
        this.delegate.encodeValues(buffer, values);
    }

    @Override
    public List<Short> decodeValues(Buffer buffer, int numValues) {
        return this.delegate.decodeValues(buffer, numValues);
    }

    public static UnsignedShortEncodingStrategy getFromCode(int code) {
        for (UnsignedShortEncodingStrategy encodingStrategy : UnsignedShortEncodingStrategy.values()) {
            if (encodingStrategy.getCode() == (code & 0xFF)) {
                return encodingStrategy;
            }
        }
        throw new IllegalArgumentException("Unknown code : " + code);
    }

    public static class Analyzer implements StrategyAnalyzer<Short> {

        private final EncodingStrategy<Short> bestStrategy;
        private final List<Short> values;

        private Analyzer(EncodingStrategy<Short> bestStrategy, List<Short> values) {
            this.bestStrategy = bestStrategy;
            this.values = values;
        }

        @Override
        public EncodingStrategy<Short> getBestStrategy() {
            return this.bestStrategy;
        }

        @Override
        public List<Short> getValues() {
            return this.values;
        }

        public static class Builder implements StrategyAnalyzerBuilder<Short> {

            private static final int SHORT_BYTE_SIZE = 2;

            private final List<Short> values = new ArrayList<Short>();
            private short previousValue = 0;

            private int byteSizeValue = 0;
            private int byteSizeRepeatCount = 0;

            private int repeatedValueCount = 0;

            @Override
            public StrategyAnalyzerBuilder<Short> addValue(Short value) {
                if (this.values.isEmpty()) {
                    initializeByteSizes();
                } else {
                    updateByteSizes(value);
                }
                this.previousValue = value;

                this.values.add(value);
                return this;
            }

            @Override
            public StrategyAnalyzer<Short> build() {
                if (this.repeatedValueCount > 0) {
                    this.byteSizeRepeatCount += BytesUtils.computeVar32Size(this.repeatedValueCount);
                }
                EncodingStrategy<Short> bestStrategy;
                int minimumNumBytesUsed = Collections.min(Arrays.asList(
                        this.byteSizeValue,
                        this.byteSizeRepeatCount));
                if (this.byteSizeValue == minimumNumBytesUsed) {
                    bestStrategy = NONE;
                } else {
                    bestStrategy = REPEAT_COUNT;
                }
                List<Short> values = new ArrayList<Short>(this.values);
                this.values.clear();
                return new Analyzer(bestStrategy, values);
            }

            int getByteSizeValue() {
                return byteSizeValue;
            }

            int getByteSizeRepeatCount() {
                return byteSizeRepeatCount;
            }

            private void initializeByteSizes() {
                this.byteSizeValue = SHORT_BYTE_SIZE;
                this.repeatedValueCount = 1;
                this.byteSizeRepeatCount = SHORT_BYTE_SIZE;
            }

            private void updateByteSizes(int value) {
                this.byteSizeValue += SHORT_BYTE_SIZE;
                if (this.previousValue != value) {
                    this.byteSizeRepeatCount += BytesUtils.computeVar32Size(this.repeatedValueCount);
                    this.byteSizeRepeatCount += 2;
                    this.repeatedValueCount = 1;
                } else {
                    this.repeatedValueCount++;
                }
            }
        }
    }
}
