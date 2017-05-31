package com.m2u.eyelink.agent.profiler.context.provider;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.m2u.eyelink.agent.profiler.plugin.ApplicationTypeDetector;
import com.m2u.eyelink.agent.profiler.plugin.PluginContextLoadResult;
import com.m2u.eyelink.agent.profiler.util.ApplicationServerTypeResolver;
import com.m2u.eyelink.common.service.ServiceTypeRegistryService;
import com.m2u.eyelink.config.ProfilerConfig;
import com.m2u.eyelink.trace.ServiceType;

public class ApplicationServerTypeProvider implements Provider<ServiceType> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ProfilerConfig profilerConfig;
    private final ServiceTypeRegistryService serviceTypeRegistryService;
    private final Provider<PluginContextLoadResult> pluginContextLoadResultProvider;

    @Inject
    public ApplicationServerTypeProvider(ProfilerConfig profilerConfig, ServiceTypeRegistryService serviceTypeRegistryService, Provider<PluginContextLoadResult> pluginContextLoadResultProvider) {
        if (profilerConfig == null) {
            throw new NullPointerException("profilerConfig must not be null");
        }
        if (serviceTypeRegistryService == null) {
            throw new NullPointerException("serviceTypeRegistryService must not be null");
        }
        this.profilerConfig = profilerConfig;
        this.serviceTypeRegistryService = serviceTypeRegistryService;
        this.pluginContextLoadResultProvider = pluginContextLoadResultProvider;
    }

    @Override
    public ServiceType get() {
        final ServiceType applicationServiceType = getApplicationServiceType();
        logger.info("default ApplicationServerType={}", applicationServiceType);

        PluginContextLoadResult pluginContextLoadResult = this.pluginContextLoadResultProvider.get();
        List<ApplicationTypeDetector> applicationTypeDetectorList = pluginContextLoadResult.getApplicationTypeDetectorList();
        ApplicationServerTypeResolver applicationServerTypeResolver = new ApplicationServerTypeResolver(applicationTypeDetectorList, applicationServiceType, profilerConfig.getApplicationTypeDetectOrder());
        ServiceType resolve = applicationServerTypeResolver.resolve();
        logger.info("resolved ApplicationServerType={}", resolve);
        return resolve;
    }

    private ServiceType getApplicationServiceType() {
        String applicationServerTypeString = profilerConfig.getApplicationServerType();
        return this.serviceTypeRegistryService.findServiceTypeByName(applicationServerTypeString);
    }
}
