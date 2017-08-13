package com.m2u.eyelink.collector.common.elasticsearch;

public class ElasticSearchAccessException extends RuntimeException {

    public ElasticSearchAccessException() {
    }

    public ElasticSearchAccessException(String message) {
        super(message);
    }

    public ElasticSearchAccessException(Throwable cause) {
        super(cause);
    }

    public ElasticSearchAccessException(String message, Throwable cause) {
        super(message, cause);
    }

}
