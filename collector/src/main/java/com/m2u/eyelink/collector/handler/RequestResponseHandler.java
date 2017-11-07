package com.m2u.eyelink.collector.handler;

import org.apache.thrift.TBase;
import org.springframework.stereotype.Service;

@Service
public interface RequestResponseHandler {
    TBase<?, ?> handleRequest(TBase<?, ?> tbase);

}
