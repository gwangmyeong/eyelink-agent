package com.m2u.eyelink.agent.async;

import com.m2u.eyelink.context.AsyncTraceId;

public interface AsyncTraceIdAccessor {
    void _$PINPOINT$_setAsyncTraceId(AsyncTraceId id);
    AsyncTraceId _$PINPOINT$_getAsyncTraceId();
}

