package com.m2u.eyelink.agent.interceptor.scope;

import com.m2u.eyelink.agent.interceptor.AroundInterceptor0;
import com.m2u.eyelink.logging.PLogger;
import com.m2u.eyelink.logging.PLoggerFactory;

public class ScopedInterceptor0 implements AroundInterceptor0 {
    private final PLogger logger = PLoggerFactory.getLogger(getClass());
    private final boolean debugEnabled = logger.isDebugEnabled();

    private final AroundInterceptor0 interceptor;
    private final InterceptorScope scope;
    private final ExecutionPolicy policy;
    
    public ScopedInterceptor0(AroundInterceptor0 interceptor, InterceptorScope scope, ExecutionPolicy policy) {
        if (interceptor == null) {
            throw new NullPointerException("interceptor must not be null");
        }
        if (scope == null) {
            throw new NullPointerException("scope must not be null");
        }
        if (policy == null) {
            throw new NullPointerException("policy must not be null");
        }
        this.interceptor = interceptor;
        this.scope = scope;
        this.policy = policy;
    }
    
    @Override
    public void before(Object target) {
        final InterceptorScopeInvocation transaction = scope.getCurrentInvocation();
        
        if (transaction.tryEnter(policy)) {
            this.interceptor.before(target);
        } else {
            if (debugEnabled) {
                logger.debug("tryBefore() returns false: interceptorScopeTransaction: {}, executionPoint: {}. Skip interceptor {}", transaction, policy, interceptor.getClass());
            }
        }
    }

    @Override
    public void after(Object target, Object result, Throwable throwable) {
        final InterceptorScopeInvocation transaction = scope.getCurrentInvocation();
        
        if (transaction.canLeave(policy)) {
            this.interceptor.after(target, result, throwable);
            transaction.leave(policy);
        } else {
            if (debugEnabled) {
                logger.debug("tryAfter() returns false: interceptorScopeTransaction: {}, executionPoint: {}. Skip interceptor {}", transaction, policy, interceptor.getClass());
            }
        }
    }
}
