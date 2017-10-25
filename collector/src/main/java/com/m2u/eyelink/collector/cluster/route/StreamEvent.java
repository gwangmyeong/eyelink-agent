package com.m2u.eyelink.collector.cluster.route;

import org.apache.thrift.TBase;

import com.m2u.eyelink.context.thrift.TCommandTransfer;
import com.m2u.eyelink.sender.ServerStreamChannelContext;

public class StreamEvent extends DefaultRouteEvent {

    private final ServerStreamChannelContext streamChannelContext;
    private final TBase requestObject;
    
    public StreamEvent(RouteEvent routeEvent, ServerStreamChannelContext streamChannelContext, TBase requestObject) {
        this(routeEvent.getDeliveryCommand(), streamChannelContext, requestObject);
    }

    public StreamEvent(TCommandTransfer deliveryCommand, ServerStreamChannelContext streamChannelContext, TBase requestObject) {
        super(deliveryCommand, streamChannelContext.getStreamChannel().getChannel().getRemoteAddress());

        this.streamChannelContext = streamChannelContext;
        this.requestObject = requestObject;
    }

    public ServerStreamChannelContext getStreamChannelContext() {
        return streamChannelContext;
    }
    
    public int getStreamChannelId() {
        return getStreamChannelContext().getStreamId();
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
        sb.append("streamChannelContext=").append(getStreamChannelContext());
        sb.append("streamChannelId=").append(getStreamChannelId());
        sb.append("requestObject=").append(requestObject);
        sb.append('}');
        return sb.toString();
    }

}
