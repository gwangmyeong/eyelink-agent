package com.m2u.eyelink.agent.interceptor;

import com.m2u.eyelink.context.MethodDescriptor;
import com.m2u.eyelink.context.SpanEventRecorder;
import com.m2u.eyelink.context.Trace;
import com.m2u.eyelink.context.TraceContext;
import com.m2u.eyelink.logging.PLogger;
import com.m2u.eyelink.logging.PLoggerFactory;

public abstract class SpanEventSimpleAroundInterceptorForPlugin implements AroundInterceptor {
    protected final PLogger logger = PLoggerFactory.getLogger(getClass());
    protected final boolean isDebug = logger.isDebugEnabled();

    protected final MethodDescriptor methodDescriptor;
    protected final TraceContext traceContext;

    protected SpanEventSimpleAroundInterceptorForPlugin(TraceContext traceContext, MethodDescriptor descriptor) {
        if (traceContext == null) {
            throw new NullPointerException("traceContext must not be null");
        }
        if (descriptor == null) {
            throw new NullPointerException("descriptor must not be null");
        }
        this.traceContext = traceContext;
        this.methodDescriptor = descriptor;
    }

    @Override
    public void before(Object target, Object[] args) {
        if (isDebug) {
            logBeforeInterceptor(target, args);
        }

        prepareBeforeTrace(target, args);

        final Trace trace = traceContext.currentTraceObject();
        if (trace == null) {
            return;
        }
        
        try {
            final SpanEventRecorder recorder = trace.traceBlockBegin();
            doInBeforeTrace(recorder, target, args);
        } catch (Throwable th) {
            if (logger.isWarnEnabled()) {
                logger.warn("BEFORE. Caused:{}", th.getMessage(), th);
            }
        }
    }

    protected void logBeforeInterceptor(Object target, Object[] args) {
        logger.beforeInterceptor(target, args);
    }

    protected void prepareBeforeTrace(Object target, Object[] args) {

    }

    protected abstract void doInBeforeTrace(final SpanEventRecorder recorder, final Object target, final Object[] args);

    @Override
    public void after(Object target, Object[] args, Object result, Throwable throwable) {
        if (isDebug) {
            logAfterInterceptor(target, args, result, throwable);
        }

        prepareAfterTrace(target, args, result, throwable);

        final Trace trace = traceContext.currentTraceObject();
        if (trace == null) {
            return;
        }
        try {
            final SpanEventRecorder recorder = trace.currentSpanEventRecorder();
            doInAfterTrace(recorder, target, args, result, throwable);
        } catch (Throwable th) {
            if (logger.isWarnEnabled()) {
                logger.warn("AFTER error. Caused:{}", th.getMessage(), th);
            }
        } finally {
            trace.traceBlockEnd();
        }
    }

    protected void logAfterInterceptor(Object target, Object[] args, Object result, Throwable throwable) {
        logger.afterInterceptor(target, args, result, throwable);
    }

    protected void prepareAfterTrace(Object target, Object[] args, Object result, Throwable throwable) {
    }

    protected abstract void doInAfterTrace(final SpanEventRecorder recorder, final Object target, final Object[] args, final Object result, Throwable throwable);

    protected MethodDescriptor getMethodDescriptor() {
        return methodDescriptor;
    }

    protected TraceContext getTraceContext() {
        return traceContext;
    }
}