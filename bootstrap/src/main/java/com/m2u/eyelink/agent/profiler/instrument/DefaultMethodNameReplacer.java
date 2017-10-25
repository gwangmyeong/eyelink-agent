package com.m2u.eyelink.agent.profiler.instrument;


public class DefaultMethodNameReplacer implements MethodNameReplacer {
    public static final String PREFIX = "__";
    public static final String POSTFIX = "_$$pinpoint";

    public String replaceMethodName(String methodName) {
        if (methodName == null) {
            throw new NullPointerException("methodName must not be null");
        }
        return PREFIX + methodName + POSTFIX;
    }
}