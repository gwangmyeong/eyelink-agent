package com.m2u.eyelink.collector.handler;

import org.apache.thrift.TBase;

public interface RequestResponseHandler {
    TBase<?, ?> handleRequest(TBase<?, ?> tbase);

}
