package com.m2u.eyelink.agent.profiler.context.recorder;

import com.m2u.eyelink.agent.profiler.context.Span;
import com.m2u.eyelink.context.SpanRecorder;

public interface RecorderFactory {

    SpanRecorder newSpanRecorder(final Span span, final boolean isRoot, final boolean sampling);

    WrappedSpanEventRecorder newWrappedSpanEventRecorder();
}
