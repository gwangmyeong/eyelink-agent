package com.m2u.eyelink.agent.interceptor.scope;

import com.m2u.eyelink.agent.profiler.interceptor.scope.DefaultInterceptorScope;
import com.m2u.eyelink.context.scope.PoolObjectFactory;

public class InterceptorScopeFactory implements PoolObjectFactory<String, InterceptorScope> {
    @Override
    public InterceptorScope create(String scopeName) {
        return new DefaultInterceptorScope(scopeName);
    }
}
