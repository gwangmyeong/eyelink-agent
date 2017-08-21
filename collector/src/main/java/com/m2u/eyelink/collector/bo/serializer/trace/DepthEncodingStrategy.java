package com.m2u.eyelink.collector.bo.serializer.trace;

public enum DepthEncodingStrategy {
    // 1bit
    PREV_EQUALS(0),
    RAW(1);

    private final int code;

    DepthEncodingStrategy(int code) {
        this.code = code;
    }
}
