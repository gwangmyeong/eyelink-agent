package com.m2u.eyelink.agent.profiler.context.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.m2u.eyelink.agent.profiler.context.monitor.DataSourceMonitorRegistryService;
import com.m2u.eyelink.agent.profiler.metadata.ApiMetaDataService;
import com.m2u.eyelink.agent.profiler.objectfactory.ObjectBinderFactory;
import com.m2u.eyelink.config.ProfilerConfig;
import com.m2u.eyelink.context.TraceContext;

public class ObjectBinderFactoryProvider implements Provider<ObjectBinderFactory> {

    private final ProfilerConfig profilerConfig;
    private final Provider<TraceContext> traceContextProvider;
    private final DataSourceMonitorRegistryService dataSourceMonitorRegistryService;
    private final Provider<ApiMetaDataService> apiMetaDataServiceProvider;

    @Inject
    public ObjectBinderFactoryProvider(ProfilerConfig profilerConfig, Provider<TraceContext> traceContextProvider, DataSourceMonitorRegistryService dataSourceMonitorRegistryService, Provider<ApiMetaDataService> apiMetaDataServiceProvider) {
        if (profilerConfig == null) {
            throw new NullPointerException("profilerConfig must not be null");
        }
        if (traceContextProvider == null) {
            throw new NullPointerException("traceContextProvider must not be null");
        }
        if (dataSourceMonitorRegistryService == null) {
            throw new NullPointerException("dataSourceMonitorRegistryService must not be null");
        }
        if (apiMetaDataServiceProvider == null) {
            throw new NullPointerException("apiMetaDataServiceProvider must not be null");
        }
        this.profilerConfig = profilerConfig;
        this.traceContextProvider = traceContextProvider;
        this.dataSourceMonitorRegistryService = dataSourceMonitorRegistryService;
        this.apiMetaDataServiceProvider = apiMetaDataServiceProvider;
    }

    @Override
    public ObjectBinderFactory get() {
        return new ObjectBinderFactory(profilerConfig, traceContextProvider, dataSourceMonitorRegistryService, apiMetaDataServiceProvider);
    }

}
