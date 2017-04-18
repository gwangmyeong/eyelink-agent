package com.m2u.eyelink.agent;

import java.lang.instrument.Instrumentation;
import java.util.Map;

public class ELAgentStarter {

	private static final ELLogger logger = ELLogger.getLogger(ELAgent.class
			.getName());
	
    private final Map<String, String> agentArgs;
    private final ELAgentJarFile agentJarFile;
    private final ClassPathResolver classPathResolver;
    private final Instrumentation instrumentation;
    
	public ELAgentStarter(Map<String, String> agentArgsMap,
			ELAgentJarFile agentJarFile, ClassPathResolver classPathResolver,
			Instrumentation inst) {
        if (agentArgsMap == null) {
            throw new NullPointerException("agentArgsMap must not be null");
        }
        if (agentJarFile == null) {
            throw new NullPointerException("ELAgentJarFile must not be null");
        }
        if (classPathResolver == null) {
            throw new NullPointerException("classPathResolver must not be null");
        }
        if (inst == null) {
            throw new NullPointerException("instrumentation must not be null");
        }
        this.agentArgs = agentArgsMap;
        this.agentJarFile = agentJarFile;
        this.classPathResolver = classPathResolver;
        this.instrumentation = inst;

	}

	boolean start() {
        final IdValidator idValidator = new IdValidator();
        final String agentId = idValidator.getAgentId();
        if (agentId == null) {
            return false;
        }
        final String applicationName = idValidator.getApplicationName();
        if (applicationName == null) {
            return false;
        }

		
		
		return true;
	}
}
