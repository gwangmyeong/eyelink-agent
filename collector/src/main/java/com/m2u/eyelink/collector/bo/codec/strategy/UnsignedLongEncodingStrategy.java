package com.m2u.eyelink.collector.bo.codec.strategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.m2u.eyelink.collector.bo.codec.TypedBufferHandler;
import com.m2u.eyelink.collector.bo.codec.ArithmeticOperation;
import com.m2u.eyelink.collector.bo.codec.strategy.impl.DeltaEncodingStrategy;
import com.m2u.eyelink.collector.bo.codec.strategy.impl.DeltaOfDeltaEncodingStrategy;
import com.m2u.eyelink.collector.bo.codec.strategy.impl.RepeatCountEncodingStrategy;
import com.m2u.eyelink.collector.bo.codec.strategy.impl.ValueEncodingStrategy;
import com.m2u.eyelink.util.Buffer;
import com.m2u.eyelink.util.BytesUtils;

public enum UnsignedLongEncodingStrategy implements EncodingStrategy<Long> {
    NONE(new ValueEncodingStrategy.Unsigned<Long>(TypedBufferHandler.LONG_BUFFER_HANDLER)),
    REPEAT_COUNT(new RepeatCountEncodingStrategy.Unsigned<Long>(TypedBufferHandler.LONG_BUFFER_HANDLER)),
    DELTA(new DeltaEncodingStrategy.Unsigned<Long>(TypedBufferHandler.LONG_BUFFER_HANDLER, ArithmeticOperation.LONG_OPERATIONS)),
    DELTA_OF_DELTA(new DeltaOfDeltaEncodingStrategy.Unsigned<Long>(TypedBufferHandler.LONG_BUFFER_HANDLER, ArithmeticOperation.LONG_OPERATIONS));

    private final EncodingStrategy<Long> delegate;

    UnsignedLongEncodingStrategy(EncodingStrategy<Long> delegate) {
        this.delegate = delegate;
    }

    @Override
    public byte getCode() {
        return this.delegate.getCode();
    }

    @Override
    public void encodeValues(Buffer buffer, List<Long> values) {
        this.delegate.encodeValues(buffer, values);
    }

    @Override
    public List<Long> decodeValues(Buffer buffer, int numValues) {
        return this.delegate.decodeValues(buffer, numValues);
    }

    public static UnsignedLongEncodingStrategy getFromCode(int code) {
        for (UnsignedLongEncodingStrategy encodingStrategy : UnsignedLongEncodingStrategy.values()) {
            if (encodingStrategy.getCode() == (code & 0xFF)) {
                return encodingStrategy;
            }
        }
        throw new IllegalArgumentException("Unknown code : " + code);
    }

    public static class Analyzer implements StrategyAnalyzer<Long> {

        private final EncodingStrategy<Long> bestStrategy;
        private final List<Long> values;

        private Analyzer(EncodingStrategy<Long> bestStrategy, List<Long> values) {
            this.bestStrategy = bestStrategy;
            this.values = values;
        }

        @Override
        public EncodingStrategy<Long> getBestStrategy() {
            return this.bestStrategy;
        }

        @Override
        public List<Long> getValues() {
            return this.values;
        }

        public static class Builder implements StrategyAnalyzerBuilder<Long> {

            private final List<Long> values = new ArrayList<Long>();
            private long previousValue = 0L;
            private long previousDelta = 0L;

            private int byteSizeValue = 0;
            private int byteSizeDelta = 0;
            private int byteSizeDeltaOfDelta = 0;
            private int byteSizeRepeatCount = 0;

            private int repeatedValueCount = 0;

            @Override
            public StrategyAnalyzerBuilder<Long> addValue(Long value) {
                long delta = value - this.previousValue;
                if (this.values.isEmpty()) {
                    initializeByteSizes(value);
                } else {
                    updateByteSizes(value, delta);
                    this.previousDelta = delta;
                }
                this.previousValue = value;

                this.values.add(value);
                return this;
            }

            @Override
            public StrategyAnalyzer<Long> build() {
                if (this.repeatedValueCount > 0) {
                    this.byteSizeRepeatCount += BytesUtils.computeVar32Size(this.repeatedValueCount);
                }
                EncodingStrategy<Long> bestStrategy;
                int minimumNumBytesUsed = Collections.min(Arrays.asList(
                        this.byteSizeValue,
                        this.byteSizeDelta,
                        this.byteSizeDeltaOfDelta,
                        this.byteSizeRepeatCount));
                if (this.byteSizeValue == minimumNumBytesUsed) {
                    bestStrategy = NONE;
                } else if (this.byteSizeDelta == minimumNumBytesUsed) {
                    bestStrategy = DELTA;
                } else if (this.byteSizeDeltaOfDelta == minimumNumBytesUsed) {
                    bestStrategy = DELTA_OF_DELTA;
                } else {
                    bestStrategy = REPEAT_COUNT;
                }
                List<Long> values = new ArrayList<Long>(this.values);
                this.values.clear();
                return new Analyzer(bestStrategy, values);
            }

            int getByteSizeValue() {
                return byteSizeValue;
            }

            int getByteSizeDelta() {
                return byteSizeDelta;
            }

            int getByteSizeDeltaOfDelta() {
                return byteSizeDeltaOfDelta;
            }

            int getByteSizeRepeatCount() {
                return byteSizeRepeatCount;
            }

            private void initializeByteSizes(long value) {
                int expectedNumBytesUsedByValue = expectedBytesVLength(value);
                this.byteSizeValue = expectedNumBytesUsedByValue;
                this.byteSizeDelta = expectedNumBytesUsedByValue;
                this.byteSizeDeltaOfDelta = expectedNumBytesUsedByValue;
                this.repeatedValueCount = 1;
                this.byteSizeRepeatCount = expectedNumBytesUsedByValue;
            }

            private void updateByteSizes(long value, long delta) {
                int expectedNumBytesUsedByValue = expectedBytesVLength(value);
                this.byteSizeValue += expectedNumBytesUsedByValue;
                this.byteSizeDelta += expectedBytesVLength(value ^ this.previousValue);
                this.byteSizeDeltaOfDelta += expectedBytesSVLength(delta - this.previousDelta);
                if (this.previousValue != value) {
                    this.byteSizeRepeatCount += BytesUtils.computeVar32Size(this.repeatedValueCount);
                    this.byteSizeRepeatCount += expectedNumBytesUsedByValue;
                    this.repeatedValueCount = 1;
                } else {
                    this.repeatedValueCount++;
                }
            }

            private int expectedBytesVLength(long value) {
                return BytesUtils.computeVar64Size(value);
            }

            private int expectedBytesSVLength(long value) {
                return expectedBytesVLength(BytesUtils.longToZigZag(value));
            }
        }
    }
}
