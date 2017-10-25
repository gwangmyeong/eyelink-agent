package com.m2u.eyelink.agent.instrument;

import com.m2u.eyelink.agent.interceptor.scope.ExecutionPolicy;
import com.m2u.eyelink.agent.interceptor.scope.InterceptorScope;
import com.m2u.eyelink.context.MethodDescriptor;

public interface InstrumentMethod {
    String getName();

    String[] getParameterTypes();
    
    String getReturnType();

    int getModifiers();
    
    boolean isConstructor();
    
    MethodDescriptor getDescriptor();

    int addInterceptor(String interceptorClassName) throws InstrumentException;

    int addInterceptor(String interceptorClassName, Object[] constructorArgs) throws InstrumentException;


    int addScopedInterceptor(String interceptorClassName, String scopeName) throws InstrumentException;

    int addScopedInterceptor(String interceptorClassName, InterceptorScope scope) throws InstrumentException;


    int addScopedInterceptor(String interceptorClassName, String scopeName, ExecutionPolicy executionPolicy) throws InstrumentException;

    int addScopedInterceptor(String interceptorClassName, InterceptorScope scope, ExecutionPolicy executionPolicy) throws InstrumentException;


    int addScopedInterceptor(String interceptorClassName, Object[] constructorArgs, String scopeName) throws InstrumentException;

    int addScopedInterceptor(String interceptorClassName, Object[] constructorArgs, InterceptorScope scope) throws InstrumentException;


    int addScopedInterceptor(String interceptorClassName, Object[] constructorArgs, String scopeName, ExecutionPolicy executionPolicy) throws InstrumentException;

    int addScopedInterceptor(String interceptorClassName, Object[] constructorArgs, InterceptorScope scope, ExecutionPolicy executionPolicy) throws InstrumentException;
    
    void addInterceptor(int interceptorId) throws InstrumentException;
}
