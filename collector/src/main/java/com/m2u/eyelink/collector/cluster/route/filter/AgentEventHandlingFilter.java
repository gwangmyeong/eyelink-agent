package com.m2u.eyelink.collector.cluster.route.filter;

import org.springframework.beans.factory.annotation.Autowired;

import com.m2u.eyelink.collector.cluster.route.ResponseEvent;
import com.m2u.eyelink.collector.rpc.handler.AgentEventHandler;

public class AgentEventHandlingFilter implements RouteFilter<ResponseEvent> {

    @Autowired
    private AgentEventHandler agentEventHandler;

    @Override
    public void doEvent(ResponseEvent event) {
        if (event == null) {
            return;
        }
        final long eventTimestamp = System.currentTimeMillis();
        this.agentEventHandler.handleResponseEvent(event, eventTimestamp);
    }

}
