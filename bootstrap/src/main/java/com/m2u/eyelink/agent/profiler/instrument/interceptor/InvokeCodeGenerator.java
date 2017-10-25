package com.m2u.eyelink.agent.profiler.instrument.interceptor;

import java.lang.reflect.Modifier;

import com.m2u.eyelink.agent.instrument.InstrumentMethod;
import com.m2u.eyelink.agent.interceptor.InterceptorInvokerHelper;
import com.m2u.eyelink.agent.profiler.JavaAssistUtils;
import com.m2u.eyelink.agent.profiler.interceptor.InterceptorDefinition;
import com.m2u.eyelink.agent.profiler.interceptor.registry.InterceptorRegistry;
import com.m2u.eyelink.agent.profiler.metadata.ApiMetaDataService;
import com.m2u.eyelink.context.MethodDescriptor;

public class InvokeCodeGenerator {
    protected final ApiMetaDataService apiMetaDataService;
    protected final InterceptorDefinition interceptorDefinition;
    protected final InstrumentMethod targetMethod;
    protected final int interceptorId;

    public InvokeCodeGenerator(int interceptorId, InterceptorDefinition interceptorDefinition, InstrumentMethod targetMethod, ApiMetaDataService apiMetaDataService) {
        if (interceptorDefinition == null) {
            throw new NullPointerException("interceptorDefinition must not be null");
        }
        if (targetMethod == null) {
            throw new NullPointerException("targetMethod must not be null");
        }
        if (apiMetaDataService == null) {
            throw new NullPointerException("apiMetaDataService must not be null");
        }

        this.interceptorDefinition = interceptorDefinition;
        this.targetMethod = targetMethod;
        this.interceptorId = interceptorId;
        this.apiMetaDataService = apiMetaDataService;

    }

    protected String getInterceptorType() {
//        return interceptorDefinition.getInterceptorClass().getName();
        return interceptorDefinition.getInterceptorBaseClass().getName();
    }

    protected String getParameterTypes() {
        String[] parameterTypes = targetMethod.getParameterTypes();
        return JavaAssistUtils.getParameterDescription(parameterTypes);
    }

    protected String getTarget() {
        return Modifier.isStatic(targetMethod.getModifiers()) ? "null" : "this";
    }

    protected String getArguments() {
        if (targetMethod.getParameterTypes().length == 0) {
            return "null";
        }

        return "$args";
    }
    
    protected int getApiId() {
        final MethodDescriptor descriptor = targetMethod.getDescriptor();
        final int apiId = apiMetaDataService.cacheApi(descriptor);
        return apiId;
    }
    
    protected String getInterceptorInvokerHelperClassName() {
        return InterceptorInvokerHelper.class.getName();
    }

    protected String getInterceptorRegistryClassName() {
        return InterceptorRegistry.class.getName();
    }
    
    protected String getInterceptorVar() {
        return getInterceptorVar(interceptorId);
    }
    
    public static String getInterceptorVar(int interceptorId) {
        return "_$PINPOINT$_interceptor" + interceptorId;
    }
}