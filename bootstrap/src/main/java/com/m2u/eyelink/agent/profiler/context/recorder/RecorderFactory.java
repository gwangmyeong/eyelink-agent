package com.m2u.eyelink.agent.profiler.context.recorder;

import com.m2u.eyelink.context.Span;
import com.m2u.eyelink.context.SpanRecorder;
import com.m2u.eyelink.context.WrappedSpanEventRecorder;

public interface RecorderFactory {

    SpanRecorder newSpanRecorder(final Span span, final boolean isRoot, final boolean sampling);

    WrappedSpanEventRecorder newWrappedSpanEventRecorder();
}