package com.m2u.eyelink.collector.cluster.route;

import java.net.SocketAddress;

import com.m2u.eyelink.context.thrift.TCommandTransfer;

public class DefaultRouteEvent implements RouteEvent {

    private final TCommandTransfer deliveryCommand;

    private final SocketAddress remoteAddress;

    public DefaultRouteEvent(TCommandTransfer deliveryCommand, SocketAddress remoteAddress) {
        this.deliveryCommand = deliveryCommand;
        this.remoteAddress = remoteAddress;
    }

    @Override
    public TCommandTransfer getDeliveryCommand() {
        return deliveryCommand;
    }

    @Override
    public SocketAddress getRemoteAddress() {
        return remoteAddress;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName());
        sb.append("{");
        sb.append("{remoteAddress=").append(remoteAddress).append(",");
        sb.append("applicationName=").append(deliveryCommand.getApplicationName()).append(",");
        sb.append("agentId=").append(deliveryCommand.getAgentId()).append(",");
        sb.append("startTimeStamp=").append(deliveryCommand.getStartTime());
        sb.append('}');
        return sb.toString();
    }

}
