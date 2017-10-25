package com.m2u.eyelink.context;


public interface StackOperation {
    int DEFAULT_STACKID = -1;

    int ROOT_STACKID = 0;

    SpanEventRecorder traceBlockBegin();

    SpanEventRecorder traceBlockBegin(int stackId);

    void traceBlockEnd();

    void traceBlockEnd(int stackId);

    boolean isRootStack();
    
    int getCallStackFrameId();
}
