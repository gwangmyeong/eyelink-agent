package com.m2u.eyelink.collector.common.elasticsearch;

import org.springframework.dao.UncategorizedDataAccessException;

@SuppressWarnings("serial")
public class ElasticSearchSystemException extends UncategorizedDataAccessException {

    public ElasticSearchSystemException(Exception cause) {
        super(cause.getMessage(), cause);
    }
}
