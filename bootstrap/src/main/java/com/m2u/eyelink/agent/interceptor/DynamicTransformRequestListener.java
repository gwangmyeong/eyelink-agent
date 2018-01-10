package com.m2u.eyelink.agent.interceptor;

import java.lang.instrument.ClassFileTransformer;

import com.m2u.eyelink.agent.instrument.RequestHandle;


public interface DynamicTransformRequestListener {
    RequestHandle onRetransformRequest(Class<?> target, ClassFileTransformer transformer);

    void onTransformRequest(ClassLoader classLoader, String targetClassName, ClassFileTransformer transformer);

}
