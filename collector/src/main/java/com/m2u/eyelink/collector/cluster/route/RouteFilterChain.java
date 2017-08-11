package com.m2u.eyelink.collector.cluster.route;

public interface RouteFilterChain<T extends RouteEvent> {

    void addLast(RouteFilter<T> filter);

    void doEvent(T event);

}
