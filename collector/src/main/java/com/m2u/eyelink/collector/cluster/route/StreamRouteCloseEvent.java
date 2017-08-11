package com.m2u.eyelink.collector.cluster.route;

import com.m2u.eyelink.context.thrift.TCommandTransfer;
import com.m2u.eyelink.rpc.ClientStreamChannelContext;
import com.m2u.eyelink.sender.ServerStreamChannelContext;

public class StreamRouteCloseEvent extends DefaultRouteEvent {

    private final ClientStreamChannelContext producerContext;
    private final ServerStreamChannelContext consumerContext;

    public StreamRouteCloseEvent(TCommandTransfer deliveryCommand, ClientStreamChannelContext producerContext, ServerStreamChannelContext consumerContext) {
        super(deliveryCommand, consumerContext.getStreamChannel().getChannel().getRemoteAddress());

        this.producerContext = producerContext;
        this.consumerContext = consumerContext;
    }

    public int getProducerStreamChannelId() {
        return producerContext.getStreamId();
    }
    
    public int getConsumerStreamChannelId() {
        return consumerContext.getStreamId();
    }

    public ClientStreamChannelContext getProducerContext() {
        return producerContext;
    }

    public ServerStreamChannelContext getConsumerContext() {
        return consumerContext;
    }

}
