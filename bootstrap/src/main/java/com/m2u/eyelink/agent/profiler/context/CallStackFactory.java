package com.m2u.eyelink.agent.profiler.context;

public interface CallStackFactory {
    CallStack newCallStack(Span span);
}
