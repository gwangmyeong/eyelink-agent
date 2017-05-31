package com.m2u.eyelink.agent.profiler.context.id;

import com.m2u.eyelink.context.TraceId;

public interface TraceIdFactory {
    TraceId newTraceId();

    TraceId parse(String transactionId, long parentSpanId, long spanId, short flags);

}
