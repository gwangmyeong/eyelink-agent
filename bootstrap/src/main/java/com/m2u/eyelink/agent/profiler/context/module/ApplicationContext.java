package com.m2u.eyelink.agent.profiler.context.module;

import com.m2u.eyelink.agent.instrument.DynamicTransformTrigger;
import com.m2u.eyelink.agent.profiler.ClassFileTransformerDispatcher;
import com.m2u.eyelink.agent.profiler.instrument.InstrumentEngine;
import com.m2u.eyelink.config.ProfilerConfig;
import com.m2u.eyelink.context.AgentInformation;
import com.m2u.eyelink.context.TraceContext;

public interface ApplicationContext {

    ProfilerConfig getProfilerConfig();

    TraceContext getTraceContext();

    InstrumentEngine getInstrumentEngine();


    DynamicTransformTrigger getDynamicTransformTrigger();

    ClassFileTransformerDispatcher getClassFileTransformerDispatcher();

    AgentInformation getAgentInformation();


    void start();

    void close();
}
