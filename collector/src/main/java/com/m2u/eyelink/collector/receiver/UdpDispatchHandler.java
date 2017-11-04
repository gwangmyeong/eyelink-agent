package com.m2u.eyelink.collector.receiver;

import org.apache.thrift.TBase;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.m2u.eyelink.collector.handler.Handler;
import com.m2u.eyelink.thrift.TAgentStat;
import com.m2u.eyelink.thrift.TAgentStatBatch;

public class UdpDispatchHandler extends AbstractDispatchHandler {

    @Autowired
    @Qualifier("agentStatHandlerFactory")
    private Handler agentStatHandler;


    public UdpDispatchHandler() {
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    @Override
    Handler getHandler(TBase<?, ?> tBase) {

        // To change below code to switch table make it a little bit faster.
        // FIXME (2014.08) Legacy - TAgentStats should not be sent over the wire.
        if (tBase instanceof TAgentStat || tBase instanceof TAgentStatBatch) {
            return agentStatHandler;
        }
        return null;
    }

}
