package com.m2u.eyelink.collector.cluster.route;

import java.net.SocketAddress;

import com.m2u.eyelink.context.thrift.TCommandTransfer;
import com.m2u.eyelink.context.thrift.TCommandTransferResponse;

public class ResponseEvent extends DefaultRouteEvent {

    private final int requestId;

    private final TCommandTransferResponse routeResult;

    public ResponseEvent(RouteEvent routeEvent, int requestId, TCommandTransferResponse routeResult) {
        this(routeEvent.getDeliveryCommand(), routeEvent.getRemoteAddress(), requestId, routeResult);
    }

    public ResponseEvent(TCommandTransfer deliveryCommand, SocketAddress remoteAddress, int requestId, TCommandTransferResponse routeResult) {
        super(deliveryCommand, remoteAddress);

        this.requestId = requestId;
        this.routeResult = routeResult;
    }

    public int getRequestId() {
        return requestId;
    }

    public TCommandTransferResponse getRouteResult() {
        return routeResult;
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
        sb.append("routeResult=").append(routeResult);
        sb.append('}');
        
        return sb.toString();
    }

}
