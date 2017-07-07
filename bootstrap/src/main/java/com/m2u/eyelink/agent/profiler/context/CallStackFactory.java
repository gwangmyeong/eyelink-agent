package com.m2u.eyelink.agent.profiler.context;

import com.m2u.eyelink.context.CallStack;

public interface CallStackFactory {
    CallStack newCallStack(Span span);
}
