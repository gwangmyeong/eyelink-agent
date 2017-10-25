package com.m2u.eyelink.collector.bo.serializer.trace;

public enum StartElapsedTimeEncodingStrategy {
    // 1 bit
    PREV_EQUALS(0),
    PREV_DELTA(1);

    private final int code;

    StartElapsedTimeEncodingStrategy(int code) {
        this.code = code;
    }
}
