package com.m2u.eyelink.agent.profiler.instrument;

import java.util.jar.JarFile;

import com.m2u.eyelink.agent.instrument.InstrumentClass;
import com.m2u.eyelink.agent.instrument.InstrumentContext;
import com.m2u.eyelink.agent.instrument.NotFoundInstrumentException;

public interface InstrumentEngine {

    InstrumentClass getClass(InstrumentContext instrumentContext, ClassLoader classLoader, String classInternalName, byte[] classFileBuffer) throws NotFoundInstrumentException;

    boolean hasClass(ClassLoader classLoader, String classBinaryName);

    void appendToBootstrapClassPath(JarFile jarFile);
}
