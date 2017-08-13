package com.m2u.eyelink.collector.common.elasticsearch;

public class ScanTaskException extends RuntimeException {

    private static final long serialVersionUID = 8554224683436066023L;

    public ScanTaskException(Throwable th) {
        super(th);
    }

    public ScanTaskException(String message, Throwable th) {
        super(message, th);
    }
}
