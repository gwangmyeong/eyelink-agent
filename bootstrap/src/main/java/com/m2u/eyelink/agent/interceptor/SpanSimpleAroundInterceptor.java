package com.m2u.eyelink.agent.interceptor;

import com.m2u.eyelink.context.MethodDescriptor;
import com.m2u.eyelink.context.SpanRecorder;
import com.m2u.eyelink.context.Trace;
import com.m2u.eyelink.context.TraceContext;
import com.m2u.eyelink.logging.PLogger;
import com.m2u.eyelink.logging.PLoggerFactory;

public abstract class SpanSimpleAroundInterceptor implements AroundInterceptor {
    protected final PLogger logger;
    protected final boolean isDebug;

    protected final MethodDescriptor methodDescriptor;
    protected final TraceContext traceContext;

    protected SpanSimpleAroundInterceptor(TraceContext traceContext, MethodDescriptor methodDescriptor, Class<? extends SpanSimpleAroundInterceptor> childClazz) {
        this.traceContext = traceContext;
        this.methodDescriptor = methodDescriptor;
        this.logger = PLoggerFactory.getLogger(childClazz);
        this.isDebug = logger.isDebugEnabled();
    }

    @Override
    public void before(Object target, Object[] args) {
        if (isDebug) {
            logger.beforeInterceptor(target, args);
        }

        try {
            final Trace trace = createTrace(target, args);
            if (trace == null) {
                return;
            }
            // TODO STATDISABLE this logic was added to disable statistics tracing
            if (!trace.canSampled()) {
                return;
            }
            // ------------------------------------------------------
            final SpanRecorder recorder = trace.getSpanRecorder();
            doInBeforeTrace(recorder, target, args);
        } catch (Throwable th) {
            if (logger.isWarnEnabled()) {
                logger.warn("BEFORE. Caused:{}", th.getMessage(), th);
            }
        }
    }

    protected abstract void doInBeforeTrace(final SpanRecorder recorder, Object target, final Object[] args);

    protected abstract Trace createTrace(final Object target, final Object[] args);

    @Override
    public void after(Object target, Object[] args, Object result, Throwable throwable) {
        if (isDebug) {
            logger.afterInterceptor(target, args, result, throwable);
        }

        final Trace trace = traceContext.currentRawTraceObject();
        if (trace == null) {
            return;
        }
        
        // TODO STATDISABLE this logic was added to disable statistics tracing
        if (!trace.canSampled()) {
            traceContext.removeTraceObject();
            return;
        }
        // ------------------------------------------------------
        try {
            final SpanRecorder recorder = trace.getSpanRecorder();
            doInAfterTrace(recorder, target, args, result, throwable);
        } catch (Throwable th) {
            if (logger.isWarnEnabled()) {
                logger.warn("AFTER. Caused:{}", th.getMessage(), th);
            }
        } finally {
            traceContext.removeTraceObject();
            deleteTrace(trace, target, args, result, throwable);
        }
    }

    protected abstract void doInAfterTrace(final SpanRecorder recorder, final Object target, final Object[] args, final Object result, Throwable throwable);

    protected void deleteTrace(final Trace trace, final Object target, final Object[] args, final Object result, Throwable throwable) {
        trace.close();
    }
}
