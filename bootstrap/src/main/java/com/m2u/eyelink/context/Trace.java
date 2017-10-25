package com.m2u.eyelink.context;

import com.m2u.eyelink.annotations.InterfaceAudience;

public interface Trace extends StackOperation {
    // ----------------------------------------------
    // activeTrace related api
    // TODO extract interface???
    long getId();

    long getStartTime();

    Thread getBindThread();

    //------------------------------------------------

    TraceId getTraceId();

    AsyncTraceId getAsyncTraceId();

    /**
     * internal experimental api
     */
    @InterfaceAudience.LimitedPrivate("vert.x")
    AsyncTraceId getAsyncTraceId(boolean closeable);

    boolean canSampled();

    boolean isRoot();

    boolean isAsync();
    
    SpanRecorder getSpanRecorder();
    
    SpanEventRecorder currentSpanEventRecorder();
    
    void close();

    /**
     * internal experimental api
     */
    @InterfaceAudience.LimitedPrivate("vert.x")
    void flush();

    TraceScope getScope(String name);

    TraceScope addScope(String name);
}
