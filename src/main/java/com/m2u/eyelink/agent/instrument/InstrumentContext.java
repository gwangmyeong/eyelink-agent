package com.m2u.eyelink.agent.instrument;

import java.io.InputStream;

public interface InstrumentContext {

    TraceContext getTraceContext();

    InstrumentClass getInstrumentClass(ClassLoader classLoader, String className, byte[] classfileBuffer);

    boolean exist(ClassLoader classLoader, String className);

    InterceptorScope getInterceptorScope(String name);

    <T> Class<? extends T> injectClass(ClassLoader targetClassLoader, String className);

    InputStream getResourceAsStream(ClassLoader targetClassLoader, String classPath);

    void addClassFileTransformer(ClassLoader classLoader, String targetClassName, TransformCallback transformCallback);

    void addClassFileTransformer(String targetClassName, TransformCallback transformCallback);

    void retransform(Class<?> target, TransformCallback transformCallback);

}
