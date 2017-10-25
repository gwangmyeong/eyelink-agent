package com.m2u.eyelink.agent.profiler.context.provider;

import java.net.URL;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.m2u.eyelink.agent.instrument.DynamicTransformTrigger;
import com.m2u.eyelink.agent.profiler.context.module.PluginJars;
import com.m2u.eyelink.agent.profiler.instrument.InstrumentEngine;
import com.m2u.eyelink.agent.profiler.plugin.DefaultPluginContextLoadResult;
import com.m2u.eyelink.agent.profiler.plugin.PluginContextLoadResult;
import com.m2u.eyelink.config.ProfilerConfig;

public class PluginContextLoadResultProvider implements Provider<PluginContextLoadResult> {

    private final ProfilerConfig profilerConfig;
    private final InstrumentEngine instrumentEngine;
    private final URL[] pluginJars;
    private final DynamicTransformTrigger dynamicTransformTrigger;

    @Inject
    public PluginContextLoadResultProvider(ProfilerConfig profilerConfig, DynamicTransformTrigger dynamicTransformTrigger, InstrumentEngine instrumentEngine,
                                           @PluginJars URL[] pluginJars) {
        if (profilerConfig == null) {
            throw new NullPointerException("profilerConfig must not be null");
        }
        if (dynamicTransformTrigger == null) {
            throw new NullPointerException("dynamicTransformTrigger must not be null");
        }
        if (instrumentEngine == null) {
            throw new NullPointerException("instrumentEngine must not be null");
        }
        if (pluginJars == null) {
            throw new NullPointerException("pluginJars must not be null");
        }

        this.profilerConfig = profilerConfig;
        this.dynamicTransformTrigger = dynamicTransformTrigger;

        this.instrumentEngine = instrumentEngine;
        this.pluginJars = pluginJars;
    }

    @Override
    public PluginContextLoadResult get() {
        return new DefaultPluginContextLoadResult(profilerConfig, dynamicTransformTrigger, instrumentEngine, pluginJars);

    }
}
