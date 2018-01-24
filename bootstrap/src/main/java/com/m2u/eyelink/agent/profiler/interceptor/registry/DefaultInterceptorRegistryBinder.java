package com.m2u.eyelink.agent.profiler.interceptor.registry;

import com.m2u.eyelink.agent.interceptor.registry.DefaultInterceptorRegistryAdaptor;
import com.m2u.eyelink.agent.interceptor.registry.InterceptorRegistry;
import com.m2u.eyelink.agent.interceptor.registry.InterceptorRegistryAdaptor;

public class DefaultInterceptorRegistryBinder implements InterceptorRegistryBinder {

    public final static int DEFAULT_MAX = 8192;

    private final Object lock = new Object();
    private final InterceptorRegistryAdaptor interceptorRegistryAdaptor;

    public DefaultInterceptorRegistryBinder() {
        this(DEFAULT_MAX);
    }

    public DefaultInterceptorRegistryBinder(int maxRegistrySize) {
        this.interceptorRegistryAdaptor = new DefaultInterceptorRegistryAdaptor(maxRegistrySize);
    }

    @Override
    public void bind() {
        InterceptorRegistry.bind(interceptorRegistryAdaptor, lock);
    }

    @Override
    public void unbind() {
        InterceptorRegistry.unbind(lock);
    }

    public InterceptorRegistryAdaptor getInterceptorRegistryAdaptor() {
        return interceptorRegistryAdaptor;
    }

    @Override
    public String getInterceptorRegistryClassName() {
        return InterceptorRegistry.class.getName();
    }
}
