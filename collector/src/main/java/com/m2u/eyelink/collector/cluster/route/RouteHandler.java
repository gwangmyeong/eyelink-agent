package com.m2u.eyelink.collector.cluster.route;

import com.m2u.eyelink.context.thrift.TCommandTransferResponse;

public interface RouteHandler<T extends RouteEvent> {

    void addRequestFilter(RouteFilter<T> filter);

    void addResponseFilter(RouteFilter<ResponseEvent> filter);

    TCommandTransferResponse onRoute(T event);

}
