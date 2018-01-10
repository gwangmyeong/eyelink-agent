package com.m2u.eyelink.agent.profiler.instrument;

import java.io.InputStream;

import com.m2u.eyelink.agent.instrument.InstrumentClass;
import com.m2u.eyelink.agent.instrument.InstrumentContext;
import com.m2u.eyelink.agent.instrument.transformer.TransformCallback;
import com.m2u.eyelink.agent.interceptor.scope.InterceptorScope;

public class GuardInstrumentContext implements InstrumentContext {
    private final InstrumentContext instrumentContext;
    private boolean closed = false;

    public GuardInstrumentContext(InstrumentContext instrumentContext) {
        if (instrumentContext == null) {
            throw new NullPointerException("instrumentContext must not be null");
        }

        this.instrumentContext = instrumentContext;
    }


    @Override
    public InstrumentClass getInstrumentClass(ClassLoader classLoader, String className, byte[] classfileBuffer) {
        checkOpen();
        return instrumentContext.getInstrumentClass(classLoader, className, classfileBuffer);
    }

    @Override
    public boolean exist(ClassLoader classLoader, String className) {
        checkOpen();
        return instrumentContext.exist(classLoader, className);
    }

    @Override
    public InterceptorScope getInterceptorScope(String name) {
        checkOpen();
        return instrumentContext.getInterceptorScope(name);
    }

    @Override
    public <T> Class<? extends T> injectClass(ClassLoader targetClassLoader, String className) {
        checkOpen();
        return instrumentContext.injectClass(targetClassLoader, className);
    }

    @Override
    public InputStream getResourceAsStream(ClassLoader targetClassLoader, String classPath) {
        checkOpen();
        return instrumentContext.getResourceAsStream(targetClassLoader, classPath);
    }

    @Override
    public void addClassFileTransformer(ClassLoader classLoader, String targetClassName, TransformCallback transformCallback) {
        checkOpen();
        instrumentContext.addClassFileTransformer(classLoader, targetClassName, transformCallback);
    }

    @Override
    public void addClassFileTransformer(String targetClassName, TransformCallback transformCallback) {
        checkOpen();
        instrumentContext.addClassFileTransformer(targetClassName, transformCallback);
    }

    @Override
    public void retransform(Class<?> target, TransformCallback transformCallback) {
        checkOpen();
        instrumentContext.retransform(target, transformCallback);
    }

    public void close() {
        this.closed = true;
    }

    private void checkOpen() {
        if (closed) {
            throw new IllegalStateException("instrumentContext already closed");
        }
    }
}
