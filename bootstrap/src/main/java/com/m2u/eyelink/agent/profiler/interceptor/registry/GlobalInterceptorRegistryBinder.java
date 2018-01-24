package com.m2u.eyelink.agent.profiler.interceptor.registry;

import com.m2u.eyelink.agent.interceptor.registry.GlobalInterceptorRegistry;
import com.m2u.eyelink.agent.interceptor.registry.InterceptorRegistryAdaptor;

/**
 * for test
 * @author 
 */
@Deprecated
public class GlobalInterceptorRegistryBinder implements InterceptorRegistryBinder {

    public GlobalInterceptorRegistryBinder() {
    }


    @Override
    public void bind() {
    }

    @Override
    public void unbind() {
    }

    public InterceptorRegistryAdaptor getInterceptorRegistryAdaptor() {
        return GlobalInterceptorRegistry.REGISTRY;
    }

    @Override
    public String getInterceptorRegistryClassName() {
        return GlobalInterceptorRegistry.class.getName();
    }
}
