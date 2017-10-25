package com.m2u.eyelink.collector.bo.serializer.trace;

public enum SequenceEncodingStrategy {
    // 1 bit
    PREV_ADD1(0),
    PREV_DELTA(1);

    private final int code;

    SequenceEncodingStrategy(int code) {
        this.code = code;
    }


}
