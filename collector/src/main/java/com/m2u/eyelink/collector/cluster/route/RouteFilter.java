package com.m2u.eyelink.collector.cluster.route;

import com.m2u.eyelink.collector.cluster.route.RouteEvent;

public interface RouteFilter<T extends RouteEvent> {

    void doEvent(T event);

}
