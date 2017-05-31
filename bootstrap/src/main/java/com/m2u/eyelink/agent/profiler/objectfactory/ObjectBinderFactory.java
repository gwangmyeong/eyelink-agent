package com.m2u.eyelink.agent.profiler.objectfactory;

import com.google.inject.Provider;
import com.m2u.eyelink.agent.instrument.InstrumentClass;
import com.m2u.eyelink.agent.instrument.InstrumentContext;
import com.m2u.eyelink.agent.plugin.monitor.DataSourceMonitorRegistry;
import com.m2u.eyelink.agent.profiler.context.monitor.DataSourceMonitorRegistryAdaptor;
import com.m2u.eyelink.agent.profiler.context.monitor.DataSourceMonitorRegistryService;
import com.m2u.eyelink.agent.profiler.interceptor.factory.AnnotatedInterceptorFactory;
import com.m2u.eyelink.agent.profiler.metadata.ApiMetaDataService;
import com.m2u.eyelink.config.ProfilerConfig;
import com.m2u.eyelink.context.TraceContext;

public class ObjectBinderFactory {
    private final ProfilerConfig profilerConfig;
    private final Provider<TraceContext> traceContextProvider;
    private final DataSourceMonitorRegistry dataSourceMonitorRegistry;
    private final Provider<ApiMetaDataService> apiMetaDataServiceProvider;

    public ObjectBinderFactory(ProfilerConfig profilerConfig, Provider<TraceContext> traceContextProvider, DataSourceMonitorRegistryService dataSourceMonitorRegistryService, Provider<ApiMetaDataService> apiMetaDataServiceProvider) {
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

        this.dataSourceMonitorRegistry = new DataSourceMonitorRegistryAdaptor(dataSourceMonitorRegistryService);
        this.apiMetaDataServiceProvider = apiMetaDataServiceProvider;
    }

    public AutoBindingObjectFactory newAutoBindingObjectFactory(InstrumentContext pluginContext, ClassLoader classLoader, ArgumentProvider... argumentProviders) {
        final TraceContext traceContext = this.traceContextProvider.get();
        return new AutoBindingObjectFactory(profilerConfig, traceContext, pluginContext, classLoader, argumentProviders);
    }


    public InterceptorArgumentProvider newInterceptorArgumentProvider(InstrumentClass instrumentClass) {
        ApiMetaDataService apiMetaDataService = this.apiMetaDataServiceProvider.get();
        return new InterceptorArgumentProvider(dataSourceMonitorRegistry, apiMetaDataService, instrumentClass);
    }

    public AnnotatedInterceptorFactory newAnnotatedInterceptorFactory(InstrumentContext pluginContext, boolean exceptionHandle) {
        final TraceContext traceContext = this.traceContextProvider.get();
        ApiMetaDataService apiMetaDataService = this.apiMetaDataServiceProvider.get();
        return new AnnotatedInterceptorFactory(profilerConfig, traceContext, dataSourceMonitorRegistry, apiMetaDataService, pluginContext, exceptionHandle);
    }
}
