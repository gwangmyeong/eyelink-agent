package com.m2u.eyelink.agent.interceptor.registry;

import com.m2u.eyelink.agent.interceptor.Interceptor;

/**
 * for test
 * @author 
 */
public class GlobalInterceptorRegistry {

    public static final InterceptorRegistryAdaptor REGISTRY = new DefaultInterceptorRegistryAdaptor();

    public static void bind(final InterceptorRegistryAdaptor interceptorRegistryAdaptor, final Object lock) {

    }

    public static void unbind(final Object lock) {

    }

    public static Interceptor getInterceptor(int key) {
        return REGISTRY.getInterceptor(key);
    }
}
