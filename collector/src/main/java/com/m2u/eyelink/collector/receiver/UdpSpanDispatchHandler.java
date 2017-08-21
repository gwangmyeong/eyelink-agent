package com.m2u.eyelink.collector.receiver;

import org.apache.thrift.TBase;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.m2u.eyelink.collector.handler.SimpleHandler;
import com.m2u.eyelink.context.thrift.TSpan;
import com.m2u.eyelink.context.thrift.TSpanChunk;

public class UdpSpanDispatchHandler extends AbstractDispatchHandler {


    @Autowired()
    @Qualifier("spanHandler")
    private SimpleHandler spanDataHandler;


    @Autowired()
    @Qualifier("spanChunkHandler")
    private SimpleHandler spanChunkHandler;

    public UdpSpanDispatchHandler() {
        this.logger = LoggerFactory.getLogger(this.getClass());
    }



    @Override
    SimpleHandler getSimpleHandler(TBase<?, ?> tBase) {
        if (tBase instanceof TSpan) {
            return spanDataHandler;
        }
        if (tBase instanceof TSpanChunk) {
            return spanChunkHandler;
        }

        return null;
    }
}
