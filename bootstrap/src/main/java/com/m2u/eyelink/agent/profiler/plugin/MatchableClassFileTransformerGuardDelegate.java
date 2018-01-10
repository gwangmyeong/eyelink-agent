package com.m2u.eyelink.agent.profiler.plugin;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import com.m2u.eyelink.agent.instrument.InstrumentContext;
import com.m2u.eyelink.agent.instrument.InstrumentException;
import com.m2u.eyelink.agent.instrument.matcher.Matcher;
import com.m2u.eyelink.agent.instrument.transformer.TransformCallback;
import com.m2u.eyelink.agent.profiler.instrument.GuardInstrumentor;
import com.m2u.eyelink.config.ProfilerConfig;
import com.m2u.eyelink.exception.ELAgentException;

public class MatchableClassFileTransformerGuardDelegate implements MatchableClassFileTransformer {

    private final ProfilerConfig profilerConfig;
    private final InstrumentContext instrumentContext;
    private final Matcher matcher;
    private final TransformCallback transformCallback;


    public MatchableClassFileTransformerGuardDelegate(ProfilerConfig profilerConfig, InstrumentContext instrumentContext, Matcher matcher, TransformCallback transformCallback) {
        if (profilerConfig == null) {
            throw new NullPointerException("profilerConfig must not be null");
        }
        if (instrumentContext == null) {
            throw new NullPointerException("instrumentContext must not be null");
        }
        if (matcher == null) {
            throw new NullPointerException("matcher must not be null");
        }
        if (transformCallback == null) {
            throw new NullPointerException("transformCallback must not be null");
        }
        this.profilerConfig = profilerConfig;
        this.instrumentContext = instrumentContext;
        this.matcher = matcher;
        this.transformCallback = transformCallback;
    }


    @Override
    public Matcher getMatcher() {
        return matcher;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (className == null) {
            throw new NullPointerException("className must not be null");
        }

        final GuardInstrumentor guard = new GuardInstrumentor(this.profilerConfig, this.instrumentContext);
        try {
            // WARN external plugin api
            return transformCallback.doInTransform(guard, loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
        } catch (InstrumentException e) {
            throw new ELAgentException(e);
        } finally {
            guard.close();
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MatchableClassFileTransformerGuardDelegate{");
        sb.append("matcher=").append(matcher);
        sb.append(", transformCallback=").append(transformCallback);
        sb.append('}');
        return sb.toString();
    }
}
