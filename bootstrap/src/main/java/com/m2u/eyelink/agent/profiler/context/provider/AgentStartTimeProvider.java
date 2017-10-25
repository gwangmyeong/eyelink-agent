package com.m2u.eyelink.agent.profiler.context.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.m2u.eyelink.context.RuntimeMXBeanUtils;

public class AgentStartTimeProvider implements Provider<Long> {

    @Inject
    public AgentStartTimeProvider() {
    }

    @Override
    public Long get() {
        return RuntimeMXBeanUtils.getVmStartTime();
    }
}