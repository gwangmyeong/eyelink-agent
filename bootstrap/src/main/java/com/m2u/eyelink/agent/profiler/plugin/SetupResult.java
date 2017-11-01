package com.m2u.eyelink.agent.profiler.plugin;

import java.lang.instrument.ClassFileTransformer;
import java.util.List;

import com.m2u.eyelink.agent.plugin.ApplicationTypeDetector;
import com.m2u.eyelink.agent.plugin.jdbc.JdbcUrlParserV2;

public class SetupResult {

    private final DefaultProfilerPluginSetupContext setupContext;
    private final ClassFileTransformerLoader transformerRegistry;

    public SetupResult(DefaultProfilerPluginSetupContext setupContext, ClassFileTransformerLoader transformerRegistry) {
        this.setupContext = setupContext;
        this.transformerRegistry = transformerRegistry;
    }


    public List<ApplicationTypeDetector> getApplicationTypeDetectors() {
        return this.setupContext.getApplicationTypeDetectors();
    }

    public List<JdbcUrlParserV2> getJdbcUrlParserList() {
        return this.setupContext.getJdbcUrlParserList();
    }

    public List<ClassFileTransformer> getClassTransformerList() {
        return transformerRegistry.getClassTransformerList();
    }


}
