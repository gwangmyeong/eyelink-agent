package com.m2u.eyelink.agent.profiler.interceptor.registry;

import com.m2u.eyelink.agent.interceptor.registry.InterceptorRegistryAdaptor;

public interface InterceptorRegistryBinder {

    void bind();

    void unbind();

    InterceptorRegistryAdaptor getInterceptorRegistryAdaptor();

    String getInterceptorRegistryClassName();
}

