package com.m2u.eyelink.agent.profiler.context.provider;

import java.lang.instrument.Instrumentation;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.m2u.eyelink.agent.instrument.DynamicTransformTrigger;
import com.m2u.eyelink.agent.profiler.DynamicTransformService;
import com.m2u.eyelink.agent.profiler.DynamicTransformerRegistry;

public class DynamicTransformTriggerProvider implements Provider<DynamicTransformTrigger> {

    private final Instrumentation instrumentation;
    private final DynamicTransformerRegistry dynamicTransformerRegistry;

    @Inject
    public DynamicTransformTriggerProvider(Instrumentation instrumentation, DynamicTransformerRegistry dynamicTransformerRegistry) {
        if (instrumentation == null) {
            throw new NullPointerException("instrumentation must not be null");
        }
        if (dynamicTransformerRegistry == null) {
            throw new NullPointerException("dynamicTransformerRegistry must not be null");
        }

        this.instrumentation = instrumentation;
        this.dynamicTransformerRegistry = dynamicTransformerRegistry;
    }

    @Override
    public DynamicTransformTrigger get() {
        return new DynamicTransformService(instrumentation, dynamicTransformerRegistry);
    }
}
