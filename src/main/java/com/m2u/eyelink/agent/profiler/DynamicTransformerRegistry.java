package com.m2u.eyelink.agent.profiler;

import java.lang.instrument.ClassFileTransformer;

public interface DynamicTransformerRegistry {
	ClassFileTransformer getTransformer(ClassLoader classLoader, String targetClassName);
}
