package com.m2u.eyelink.agent.profiler.context.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.m2u.eyelink.agent.profiler.sampler.SamplerFactory;
import com.m2u.eyelink.config.ProfilerConfig;
import com.m2u.eyelink.context.Sampler;

public class SamplerProvider implements Provider<Sampler> {

    private final ProfilerConfig profilerConfig;

    @Inject
    public SamplerProvider(ProfilerConfig profilerConfig) {
        this.profilerConfig = profilerConfig;
    }

    @Override
    public Sampler get() {
        boolean samplingEnable = profilerConfig.isSamplingEnable();
        int samplingRate = profilerConfig.getSamplingRate();

        SamplerFactory samplerFactory = new SamplerFactory();
        return samplerFactory.createSampler(samplingEnable, samplingRate);
    }
}
