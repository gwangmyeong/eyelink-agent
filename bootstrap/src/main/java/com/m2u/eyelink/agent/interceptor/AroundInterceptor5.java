package com.m2u.eyelink.agent.interceptor;


public interface AroundInterceptor5 extends Interceptor {

    void before(Object target, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4);

    void after(Object target, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object result, Throwable throwable);

}
