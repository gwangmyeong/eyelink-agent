package com.m2u.eyelink.collector.cluster.route;

public interface RouteFilter<T extends RouteEvent> {

    void doEvent(T event);

}
