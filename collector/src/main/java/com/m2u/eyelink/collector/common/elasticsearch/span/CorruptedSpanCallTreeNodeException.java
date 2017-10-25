package com.m2u.eyelink.collector.common.elasticsearch.span;

public class CorruptedSpanCallTreeNodeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String title;

    public CorruptedSpanCallTreeNodeException(String title, String message) {
        super(message);
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
