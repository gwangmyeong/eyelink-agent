package com.m2u.eyelink.collector.handler;

import org.apache.thrift.TBase;

public interface SimpleHandler {
    void handleSimple(TBase<?, ?> tbase);

}
