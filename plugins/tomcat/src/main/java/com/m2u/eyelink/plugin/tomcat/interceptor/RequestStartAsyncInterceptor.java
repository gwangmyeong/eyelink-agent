package com.m2u.eyelink.plugin.tomcat.interceptor;

import com.m2u.eyelink.agent.async.AsyncTraceIdAccessor;
import com.m2u.eyelink.context.AsyncTraceId;
import com.m2u.eyelink.context.MethodDescriptor;
import com.m2u.eyelink.context.SpanEventRecorder;
import com.m2u.eyelink.context.Trace;
import com.m2u.eyelink.context.TraceContext;
import com.m2u.eyelink.logging.PLogger;
import com.m2u.eyelink.logging.PLoggerFactory;
import com.m2u.eyelink.plugin.tomcat.AsyncAccessor;
import com.m2u.eyelink.plugin.tomcat.TomcatConstants;

public class RequestStartAsyncInterceptor implements AroundInterceptor {

    private PLogger logger = PLoggerFactory.getLogger(this.getClass());
    private boolean isDebug = logger.isDebugEnabled();

    private TraceContext traceContext;
    private MethodDescriptor descriptor;

    public RequestStartAsyncInterceptor(TraceContext context, MethodDescriptor descriptor) {
        this.traceContext = context;
        this.descriptor = descriptor;
    }

    @Override
    public void before(Object target, Object[] args) {
        if (isDebug) {
            logger.beforeInterceptor(target, "", descriptor.getMethodName(), "", args);
        }

        final Trace trace = traceContext.currentTraceObject();
        if (trace == null) {
            return;
        }
        trace.traceBlockBegin();
    }

    @Override
    public void after(Object target, Object[] args, Object result, Throwable throwable) {
        if (isDebug) {
            logger.afterInterceptor(target, "", descriptor.getMethodName(), "", args);
        }

        final Trace trace = traceContext.currentTraceObject();
        if (trace == null) {
            return;
        }

        try {
            SpanEventRecorder recorder = trace.currentSpanEventRecorder();
            if (validate(target, result, throwable)) {
                ((AsyncAccessor)target)._$PINPOINT$_setAsync(Boolean.TRUE);

                // make asynchronous trace-id
                final AsyncTraceId asyncTraceId = trace.getAsyncTraceId();
                recorder.recordNextAsyncId(asyncTraceId.getAsyncId());
                // result is BasicFuture
                // type check validate()
                ((AsyncTraceIdAccessor)result)._$PINPOINT$_setAsyncTraceId(asyncTraceId);
                if (isDebug) {
                    logger.debug("Set asyncTraceId metadata {}", asyncTraceId);
                }
            }

            recorder.recordServiceType(TomcatConstants.TOMCAT_METHOD);
            recorder.recordApi(descriptor);
            recorder.recordException(throwable);
        } catch (Throwable t) {
            logger.warn("Failed to AFTER process. {}", t.getMessage(), t);
        } finally {
            trace.traceBlockEnd();
        }
    }

    private boolean validate(final Object target, final Object result, final Throwable throwable) {
        if (throwable != null || result == null) {
            return false;
        }

        if (!(target instanceof AsyncAccessor)) {
            logger.debug("Invalid target object. Need field accessor({}).", AsyncAccessor.class.getName());
            return false;
        }

        if (!(result instanceof AsyncTraceIdAccessor)) {
            logger.debug("Invalid target object. Need metadata accessor({}).", AsyncTraceIdAccessor.class.getName());
            return false;
        }

        return true;
    }
}