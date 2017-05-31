package com.m2u.eyelink.agent.profiler.context.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.m2u.eyelink.agent.instrument.DynamicTransformTrigger;
import com.m2u.eyelink.agent.profiler.ClassFileTransformerDispatcher;
import com.m2u.eyelink.agent.profiler.DefaultClassFileTransformerDispatcher;
import com.m2u.eyelink.agent.profiler.DynamicTransformerRegistry;
import com.m2u.eyelink.agent.profiler.instrument.InstrumentEngine;
import com.m2u.eyelink.agent.profiler.plugin.PluginContextLoadResult;
import com.m2u.eyelink.config.ProfilerConfig;

public class ClassFileTransformerDispatcherProvider implements Provider<ClassFileTransformerDispatcher> {

    private final ProfilerConfig profilerConfig;
    private final PluginContextLoadResult pluginContextLoadResult;
    private final InstrumentEngine instrumentEngine;
    private final DynamicTransformTrigger dynamicTransformTrigger;
    private final DynamicTransformerRegistry dynamicTransformerRegistry;

    @Inject
    public ClassFileTransformerDispatcherProvider(ProfilerConfig profilerConfig, InstrumentEngine instrumentEngine, PluginContextLoadResult pluginContextLoadResult,
                                                  DynamicTransformTrigger dynamicTransformTrigger, DynamicTransformerRegistry dynamicTransformerRegistry) {
        if (profilerConfig == null) {
            throw new NullPointerException("profilerConfig must not be null");
        }
        if (instrumentEngine == null) {
            throw new NullPointerException("instrumentEngine must not be null");
        }
        if (pluginContextLoadResult == null) {
            throw new NullPointerException("pluginContextLoadResult must not be null");
        }
        if (dynamicTransformTrigger == null) {
            throw new NullPointerException("dynamicTransformTrigger must not be null");
        }
        if (dynamicTransformerRegistry == null) {
            throw new NullPointerException("dynamicTransformerRegistry must not be null");
        }
        this.profilerConfig = profilerConfig;
        this.instrumentEngine = instrumentEngine;
        this.pluginContextLoadResult = pluginContextLoadResult;
        this.dynamicTransformTrigger = dynamicTransformTrigger;
        this.dynamicTransformerRegistry = dynamicTransformerRegistry;
    }

    @Override
    public ClassFileTransformerDispatcher get() {
        return new DefaultClassFileTransformerDispatcher(profilerConfig, pluginContextLoadResult, instrumentEngine, dynamicTransformTrigger, dynamicTransformerRegistry);
    }
}
