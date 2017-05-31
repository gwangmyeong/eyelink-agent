package com.m2u.eyelink.agent.profiler;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public interface ClassFileTransformerDispatcher extends ClassFileTransformer {
    @Override
    byte[] transform(ClassLoader classLoader, String classInternalName, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classFileBuffer) throws IllegalClassFormatException;

}
