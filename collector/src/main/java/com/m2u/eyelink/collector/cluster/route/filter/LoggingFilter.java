package com.m2u.eyelink.collector.cluster.route.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.collector.cluster.route.RouteEvent;

public class LoggingFilter<T extends RouteEvent> implements RouteFilter<T> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void doEvent(T event) {
        logger.info("{} doEvent {}.", this.getClass().getSimpleName(), event);
    }

}
