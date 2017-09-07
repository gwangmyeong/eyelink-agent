package com.m2u.eyelink.collector.common.elasticsearch.span;

public class SpanResult {
    private int completeType;
    private CallTreeIterator callTreeIterator;

    public SpanResult(int completeType, CallTreeIterator callTreeIterator) {
        if (callTreeIterator == null) {
            throw new NullPointerException("spanAlignList must not be null");
        }
        this.completeType = completeType;
        this.callTreeIterator = callTreeIterator;
    }

    public int getCompleteType() {
        return completeType;
    }

    public CallTreeIterator getCallTree() {
        return callTreeIterator;
    }

    public String getCompleteTypeString() {
        switch (completeType) {
            case SpanAligner2.BEST_MATCH:
                return "Complete";
            case SpanAligner2.START_TIME_MATCH:
                return "Progress";
            case SpanAligner2.FAIL_MATCH:
                return "Error";
        }
        return "Error";
    }
}
