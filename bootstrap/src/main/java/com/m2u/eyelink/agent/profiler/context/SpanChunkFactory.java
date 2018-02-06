package com.m2u.eyelink.agent.profiler.context;

import java.util.List;

public interface SpanChunkFactory {

    SpanChunk create(final List<SpanEvent> flushData);
}
