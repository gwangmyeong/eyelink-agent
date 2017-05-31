package com.m2u.eyelink.agent.profiler.context.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.m2u.eyelink.agent.profiler.context.active.ActiveTraceRepository;
import com.m2u.eyelink.agent.profiler.context.active.DefaultActiveTraceRepository;
import com.m2u.eyelink.config.ProfilerConfig;

public class ActiveTraceRepositoryProvider implements Provider<ActiveTraceRepository> {

    private final ProfilerConfig profilerConfig;

    @Inject
    public ActiveTraceRepositoryProvider(ProfilerConfig profilerConfig) {
        if (profilerConfig == null) {
            throw new NullPointerException("profilerConfig must not be null");
        }
        this.profilerConfig = profilerConfig;
    }

    public ActiveTraceRepository get() {
        if (profilerConfig.isTraceAgentActiveThread()) {
            return new DefaultActiveTraceRepository();
        }
        return null;
    }

}
