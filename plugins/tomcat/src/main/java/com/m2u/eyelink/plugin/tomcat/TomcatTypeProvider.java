package com.m2u.eyelink.plugin.tomcat;

import com.m2u.eyelink.common.trace.TraceMetadataProvider;
import com.m2u.eyelink.common.trace.TraceMetadataSetupContext;

public class TomcatTypeProvider implements TraceMetadataProvider {

    @Override
    public void setup(TraceMetadataSetupContext context) {
    	System.out.println("=====> tomcat TomcatTypeProvider");
        context.addServiceType(TomcatConstants.TOMCAT);
        context.addServiceType(TomcatConstants.TOMCAT_METHOD);
    }
}
