package com.m2u.eyelink.plugin.tomcat.interceptor;

import com.m2u.eyelink.agent.instrument.InstrumentMethod;
import com.m2u.eyelink.agent.interceptor.AroundInterceptor;
import com.m2u.eyelink.agent.interceptor.AroundInterceptor;
import com.m2u.eyelink.context.Trace;
import com.m2u.eyelink.logging.PLogger;
import com.m2u.eyelink.logging.PLoggerFactory;
import com.m2u.eyelink.plugin.tomcat.AsyncAccessor;
import com.m2u.eyelink.plugin.tomcat.TraceAccessor;

public class RequestRecycleInterceptor implements AroundInterceptor {

    private PLogger logger = PLoggerFactory.getLogger(this.getClass());

    private InstrumentMethod targetMethod;

    public RequestRecycleInterceptor(InstrumentMethod targetMethod) {
        this.targetMethod = targetMethod;
    }

    @Override
    public void before(Object target, Object[] args) {
        logger.beforeInterceptor(target, target.getClass().getName(), targetMethod.getName(), "", args);
        try {
            if (target instanceof AsyncAccessor) {
                // reset
                ((AsyncAccessor) target)._$PINPOINT$_setAsync(Boolean.FALSE);
            }

            if (target instanceof TraceAccessor) {
                final Trace trace = ((TraceAccessor) target)._$PINPOINT$_getTrace();
                if (trace != null && trace.canSampled()) {
                    // end of root span
                    trace.close();
                }
                // reset
                ((TraceAccessor) target)._$PINPOINT$_setTrace(null);
            }
        } catch (Throwable t) {
            logger.warn("Failed to BEFORE process. {}", t.getMessage(), t);
        }
    }

    @Override
    public void after(Object target, Object[] args, Object result, Throwable throwable) {
    }
}