package com.m2u.eyelink.collector.cluster.route;

import java.net.SocketAddress;

import org.apache.thrift.TBase;

import com.m2u.eyelink.thrift.TCommandTransfer; 

public class RequestEvent extends DefaultRouteEvent {

    private final int requestId;

    private final TBase requestObject;

    public RequestEvent(RouteEvent routeEvent, int requestId, TBase requestObject) {
        this(routeEvent.getDeliveryCommand(), routeEvent.getRemoteAddress(), requestId, requestObject);
    }

    public RequestEvent(TCommandTransfer deliveryCommand, SocketAddress remoteAddress, int requestId, TBase requestObject) {
        super(deliveryCommand, remoteAddress);

        this.requestId = requestId;
        this.requestObject = requestObject;
    }

    public int getRequestId() {
        return requestId;
    }

    public TBase getRequestObject() {
        return requestObject;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName());
        sb.append("{");
        sb.append("{remoteAddress=").append(getRemoteAddress()).append(",");
        sb.append("applicationName=").append(getDeliveryCommand().getApplicationName()).append(",");
        sb.append("agentId=").append(getDeliveryCommand().getAgentId()).append(",");
        sb.append("startTimeStamp=").append(getDeliveryCommand().getStartTime());
        sb.append("requestId=").append(requestId);
        sb.append("requestObject=").append(requestObject);
        sb.append('}');
        return sb.toString();
    }

}
