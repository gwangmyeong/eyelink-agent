package com.m2u.eyelink.collector.handler;

import org.apache.thrift.TBase;

public interface Handler {

    void handle(TBase<?, ?> tbase);
    
}
