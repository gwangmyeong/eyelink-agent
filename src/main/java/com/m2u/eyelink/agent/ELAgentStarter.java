package com.m2u.eyelink.agent;

import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.util.Map;

public class ELAgentStarter {

	private final ELLogger logger = ELLogger.getLogger(this.getClass()
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
		// eyelink.agentId, eyelink.applicationName 설정여부 체크
        final IdValidator idValidator = new IdValidator();
        final String agentId = idValidator.getAgentId();
        if (agentId == null) {
            return false;
        }
        final String applicationName = idValidator.getApplicationName();
        if (applicationName == null) {
            return false;
        }

        //
        URL[] pluginJars = classPathResolver.resolvePlugins();
		
		return true;
	}
}
