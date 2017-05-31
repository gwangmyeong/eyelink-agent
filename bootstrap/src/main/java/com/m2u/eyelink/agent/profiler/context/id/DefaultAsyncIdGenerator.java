package com.m2u.eyelink.agent.profiler.context.id;

import java.util.concurrent.atomic.AtomicInteger;

import com.google.inject.Inject;

public class DefaultAsyncIdGenerator implements AsyncIdGenerator {

    private final AtomicInteger asyncId = new AtomicInteger();

    @Inject
    public DefaultAsyncIdGenerator() {
    }

    @Override
    public int nextAsyncId() {
        final int id = asyncId.incrementAndGet();
        if (id == -1) {
            return asyncId.incrementAndGet();
        }
        else return id;
    }
}
