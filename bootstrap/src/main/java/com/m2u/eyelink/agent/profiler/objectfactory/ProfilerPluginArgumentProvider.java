package com.m2u.eyelink.agent.profiler.objectfactory;

import java.lang.annotation.Annotation;

import com.m2u.eyelink.agent.instrument.InstrumentContext;
import com.m2u.eyelink.agent.instrument.Instrumentor;
import com.m2u.eyelink.agent.instrument.InstrumentorDelegate;
import com.m2u.eyelink.agent.interceptor.annotation.Name;
import com.m2u.eyelink.agent.interceptor.scope.InterceptorScope;
import com.m2u.eyelink.agent.profiler.util.TypeUtils;
import com.m2u.eyelink.config.ProfilerConfig;
import com.m2u.eyelink.context.Trace;
import com.m2u.eyelink.context.TraceContext;
import com.m2u.eyelink.exception.ELAgentException;


public class ProfilerPluginArgumentProvider implements ArgumentProvider {
    private final ProfilerConfig profilerConfig;
    private final TraceContext traceContext;
    private final InstrumentContext pluginContext;

    public ProfilerPluginArgumentProvider(ProfilerConfig profilerConfig, TraceContext traceContext, InstrumentContext pluginContext) {
        if (profilerConfig == null) {
            throw new NullPointerException("profilerConfig must not be null");
        }
        if (traceContext == null) {
            throw new NullPointerException("traceContext must not be null");
        }
        if (pluginContext == null) {
            throw new NullPointerException("pluginContext must not be null");
        }
        this.profilerConfig = profilerConfig;
        this.traceContext = traceContext;
        this.pluginContext = pluginContext;

    }

    @Override
    public Option get(int index, Class<?> type, Annotation[] annotations) {
        if (type == Trace.class) {
            return Option.withValue(traceContext.currentTraceObject());
        } else if (type == TraceContext.class) {
            return Option.withValue(traceContext);
        } else if (type == Instrumentor.class) {
            final InstrumentorDelegate delegate = new InstrumentorDelegate(profilerConfig, pluginContext);
            return Option.withValue(delegate);
        } else if (type == InterceptorScope.class) {
            Name annotation = TypeUtils.findAnnotation(annotations, Name.class);
            
            if (annotation == null) {
                return Option.empty();
            }
            
            InterceptorScope scope = pluginContext.getInterceptorScope(annotation.value());
            
            if (scope == null) {
                throw new ELAgentException("No such Scope: " + annotation.value());
            }
            
            return Option.withValue(scope);
        }
        
        return Option.empty();
    }
}
