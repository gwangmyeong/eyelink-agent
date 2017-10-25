package com.m2u.eyelink.agent.interceptor.scope;

import com.m2u.eyelink.agent.interceptor.AroundInterceptor5;
import com.m2u.eyelink.agent.interceptor.InterceptorInvokerHelper;
import com.m2u.eyelink.logging.PLogger;
import com.m2u.eyelink.logging.PLoggerFactory;

public class ExceptionHandleScopedInterceptor5 implements AroundInterceptor5 {
    private final PLogger logger = PLoggerFactory.getLogger(getClass());
    private final boolean debugEnabled = logger.isDebugEnabled();

    private final AroundInterceptor5 interceptor;
    private final InterceptorScope scope;
    private final ExecutionPolicy policy;

    public ExceptionHandleScopedInterceptor5(AroundInterceptor5 interceptor, InterceptorScope scope, ExecutionPolicy policy) {
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
    public void before(Object target, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        final InterceptorScopeInvocation transaction = scope.getCurrentInvocation();
        
        if (transaction.tryEnter(policy)) {
            try {
                this.interceptor.before(target, arg0, arg1, arg2, arg3, arg4);
            } catch(Throwable t) {
                InterceptorInvokerHelper.handleException(t);
            }
        } else {
            if (debugEnabled) {
                logger.debug("tryBefore() returns false: interceptorScopeTransaction: {}, executionPoint: {}. Skip interceptor {}", transaction, policy, interceptor.getClass());
            }
        }
    }

    @Override
    public void after(Object target, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object result, Throwable throwable) {
        final InterceptorScopeInvocation transaction = scope.getCurrentInvocation();
        
        if (transaction.canLeave(policy)) {
            try {
                this.interceptor.after(target, arg0, arg1, arg2, arg3, arg4, result, throwable);
            } catch(Throwable t) {
                InterceptorInvokerHelper.handleException(t);
            } finally {
                transaction.leave(policy);
            }
        } else {
            if (debugEnabled) {
                logger.debug("tryAfter() returns false: interceptorScopeTransaction: {}, executionPoint: {}. Skip interceptor {}", transaction, policy, interceptor.getClass());
            }
        }
    }
}
