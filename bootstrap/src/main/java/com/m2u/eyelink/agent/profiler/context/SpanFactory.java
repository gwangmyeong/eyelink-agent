package com.m2u.eyelink.agent.profiler.context;

import com.m2u.eyelink.context.Span;

public interface SpanFactory {
    Span newSpan();
}
