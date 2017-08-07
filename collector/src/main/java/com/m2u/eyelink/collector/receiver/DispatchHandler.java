package com.m2u.eyelink.collector.receiver;

import org.apache.thrift.TBase;

public interface DispatchHandler {

    // Separating Send and Request. That dose not be satisfied but try to change that later.

    void dispatchSendMessage(TBase<?, ?> tBase);

    TBase dispatchRequestMessage(TBase<?, ?> tBase);

}
