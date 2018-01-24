package com.m2u.eyelink.plugin.tomcat.interceptor;

import com.m2u.eyelink.agent.interceptor.SpanAsyncEventSimpleAroundInterceptor;
import com.m2u.eyelink.agent.interceptor.annotation.Scope;
import com.m2u.eyelink.context.AsyncTraceId;
import com.m2u.eyelink.context.MethodDescriptor;
import com.m2u.eyelink.context.SpanEventRecorder;
import com.m2u.eyelink.context.TraceContext;
import com.m2u.eyelink.plugin.tomcat.TomcatConstants;

@Scope(TomcatConstants.TOMCAT_SERVLET_ASYNC_SCOPE)
public class AsyncContextImplDispatchMethodInterceptor extends SpanAsyncEventSimpleAroundInterceptor {

    public AsyncContextImplDispatchMethodInterceptor(TraceContext traceContext, MethodDescriptor methodDescriptor) {
        super(traceContext, methodDescriptor);
    }

    @Override
    protected void doInBeforeTrace(SpanEventRecorder recorder, AsyncTraceId asyncTraceId, Object target, Object[] args) {
        recorder.recordServiceType(TomcatConstants.TOMCAT_METHOD);
    }

    @Override
    protected void doInAfterTrace(SpanEventRecorder recorder, Object target, Object[] args, Object result, Throwable throwable) {
        recorder.recordApi(methodDescriptor);
        recorder.recordException(throwable);
    }
}