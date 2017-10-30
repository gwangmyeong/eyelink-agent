package com.m2u.eyelink.plugin.tomcat;

import com.m2u.eyelink.common.trace.TraceMetadataProvider;
import com.m2u.eyelink.common.trace.TraceMetadataSetupContext;
import com.m2u.eyelink.logging.ELLogger;

public class TomcatTypeProvider implements TraceMetadataProvider {
	// TODO PLogger는 agent/log/xxx.log 파일에 출력되지 않음. 
	private final ELLogger logger = ELLogger.getLogger(this.getClass()
			.getName());
//	private final PLogger logger = PLoggerFactory.getLogger(this.getClass());
	
    @Override
    public void setup(TraceMetadataSetupContext context) {
    		logger.info("TomcatTypeProvider setup() started");
        context.addServiceType(TomcatConstants.TOMCAT);
        context.addServiceType(TomcatConstants.TOMCAT_METHOD);
    }
}
