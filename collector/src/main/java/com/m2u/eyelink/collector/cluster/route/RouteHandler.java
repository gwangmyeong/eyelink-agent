package com.m2u.eyelink.collector.cluster.route;

import com.m2u.eyelink.collector.cluster.route.filter.RouteFilter;
import com.m2u.eyelink.thrift.TCommandTransferResponse; interface RouteHandler<T extends RouteEvent> {

    void addRequestFilter(RouteFilter<T> filter);

    void addResponseFilter(RouteFilter<ResponseEvent> filter);

    TCommandTransferResponse onRoute(T event);

}
