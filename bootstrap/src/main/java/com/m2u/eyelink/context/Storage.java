package com.m2u.eyelink.context;

import com.m2u.eyelink.agent.profiler.context.Span;

public interface Storage {

    /**
     *
     * @param spanEvent
     */
    void store(SpanEvent spanEvent);

    /**
     *
     * @param span
     */
    void store(Span span);

    void flush();

    void close();
}
