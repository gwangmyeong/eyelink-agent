package com.m2u.eyelink.agent.profiler.context;

import com.google.inject.Inject;
import com.m2u.eyelink.config.ProfilerConfig;
import com.m2u.eyelink.context.CallStack;

public class DefaultCallStackFactory implements CallStackFactory {

    private final int maxDepth;

    @Inject
    public DefaultCallStackFactory(ProfilerConfig profilerConfig) {
        this(profilerConfig.getCallStackMaxDepth());
    }

    public DefaultCallStackFactory(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    @Override
    public CallStack newCallStack(Span span) {
        return new CallStack(span, maxDepth);
    }
}