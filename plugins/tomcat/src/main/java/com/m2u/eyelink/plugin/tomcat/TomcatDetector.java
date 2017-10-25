package com.m2u.eyelink.plugin.tomcat;

import java.util.Arrays;
import java.util.List;

import com.m2u.eyelink.agent.profiler.plugin.ApplicationTypeDetector;
import com.m2u.eyelink.agent.resolver.ConditionProvider;
import com.m2u.eyelink.common.trace.ServiceType;

public class TomcatDetector implements ApplicationTypeDetector {
    
    private static final String DEFAULT_BOOTSTRAP_MAIN = "org.apache.catalina.startup.Bootstrap";
    
    private static final String REQUIRED_SYSTEM_PROPERTY = "catalina.home";
    
    private static final String REQUIRED_CLASS = "org.apache.catalina.startup.Bootstrap";

    private final List<String> bootstrapMains;

    public TomcatDetector(List<String> bootstrapMains) {
        if (bootstrapMains == null || bootstrapMains.isEmpty()) {
            this.bootstrapMains = Arrays.asList(DEFAULT_BOOTSTRAP_MAIN);
        } else {
            this.bootstrapMains = bootstrapMains;
        }
    }
    
    @Override
    public ServiceType getApplicationType() {
        return TomcatConstants.TOMCAT;
    }

    @Override
    public boolean detect(ConditionProvider provider) {
        return provider.checkMainClass(bootstrapMains) &&
               provider.checkSystemProperty(REQUIRED_SYSTEM_PROPERTY) &&
               provider.checkForClass(REQUIRED_CLASS);
    }

}
