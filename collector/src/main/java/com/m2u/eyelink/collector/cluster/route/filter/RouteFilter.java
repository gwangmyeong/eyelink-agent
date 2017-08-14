package com.m2u.eyelink.collector.cluster.route.filter;

import com.m2u.eyelink.collector.cluster.route.RouteEvent;

public interface RouteFilter<T extends RouteEvent> {

    void doEvent(T event);

}
