package com.m2u.eyelink.agent.profiler.context;

import java.util.List;

import com.m2u.eyelink.context.SpanEvent;

public interface SpanChunkFactory {

    SpanChunk create(final List<SpanEvent> flushData);
}
