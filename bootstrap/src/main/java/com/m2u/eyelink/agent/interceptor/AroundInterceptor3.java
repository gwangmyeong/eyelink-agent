package com.m2u.eyelink.agent.interceptor;


public interface AroundInterceptor3 extends Interceptor {
    void before(Object target, Object arg0, Object arg1, Object arg2);
    void after(Object target, Object arg0, Object arg1, Object arg2, Object result, Throwable throwable);
}
