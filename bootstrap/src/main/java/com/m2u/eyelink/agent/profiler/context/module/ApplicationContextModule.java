package com.m2u.eyelink.agent.profiler.context.module;

import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.util.List;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.m2u.eyelink.agent.AgentOption;
import com.m2u.eyelink.agent.instrument.DynamicTransformTrigger;
import com.m2u.eyelink.agent.profiler.AgentInfoSender;
import com.m2u.eyelink.agent.profiler.ClassFileTransformerDispatcher;
import com.m2u.eyelink.agent.profiler.DefaultDynamicTransformerRegistry;
import com.m2u.eyelink.agent.profiler.DynamicTransformerRegistry;
import com.m2u.eyelink.agent.profiler.JvmInformation;
import com.m2u.eyelink.agent.profiler.context.CallStackFactory;
import com.m2u.eyelink.agent.profiler.context.DefaultCallStackFactory;
import com.m2u.eyelink.agent.profiler.context.DefaultSpanChunkFactory;
import com.m2u.eyelink.agent.profiler.context.DefaultSpanFactory;
import com.m2u.eyelink.agent.profiler.context.SpanChunkFactory;
import com.m2u.eyelink.agent.profiler.context.SpanFactory;
import com.m2u.eyelink.agent.profiler.context.active.ActiveTraceRepository;
import com.m2u.eyelink.agent.profiler.context.id.AsyncIdGenerator;
import com.m2u.eyelink.agent.profiler.context.id.AtomicIdGenerator;
import com.m2u.eyelink.agent.profiler.context.id.DefaultAsyncIdGenerator;
import com.m2u.eyelink.agent.profiler.context.id.DefaultTraceIdFactory;
import com.m2u.eyelink.agent.profiler.context.id.DefaultTransactionCounter;
import com.m2u.eyelink.agent.profiler.context.id.IdGenerator;
import com.m2u.eyelink.agent.profiler.context.id.TraceIdFactory;
import com.m2u.eyelink.agent.profiler.context.id.TransactionCounter;
import com.m2u.eyelink.agent.profiler.context.monitor.DataSourceMonitorRegistryService;
import com.m2u.eyelink.agent.profiler.context.monitor.DefaultJdbcContext;
import com.m2u.eyelink.agent.profiler.context.monitor.JdbcUrlParsingService;
import com.m2u.eyelink.agent.profiler.context.provider.ActiveTraceRepositoryProvider;
import com.m2u.eyelink.agent.profiler.context.provider.AgentInfoSenderProvider;
import com.m2u.eyelink.agent.profiler.context.provider.AgentInformationProvider;
import com.m2u.eyelink.agent.profiler.context.provider.AgentStartTimeProvider;
import com.m2u.eyelink.agent.profiler.context.provider.ApiMetaDataServiceProvider;
import com.m2u.eyelink.agent.profiler.context.provider.ApplicationServerTypeProvider;
import com.m2u.eyelink.agent.profiler.context.provider.ClassFileTransformerDispatcherProvider;
import com.m2u.eyelink.agent.profiler.context.provider.CommandDispatcherProvider;
import com.m2u.eyelink.agent.profiler.context.provider.DataSourceMonitorRegistryServiceProvider;
import com.m2u.eyelink.agent.profiler.context.provider.DefaultSqlMetaDataService;
import com.m2u.eyelink.agent.profiler.context.provider.DynamicTransformTriggerProvider;
import com.m2u.eyelink.agent.profiler.context.provider.ELAgentClientFactoryProvider;
import com.m2u.eyelink.agent.profiler.context.provider.ELAgentClientProvider;
import com.m2u.eyelink.agent.profiler.context.provider.InstrumentEngineProvider;
import com.m2u.eyelink.agent.profiler.context.provider.JdbcUrlParsingServiceProvider;
import com.m2u.eyelink.agent.profiler.context.provider.JvmInformationProvider;
import com.m2u.eyelink.agent.profiler.context.provider.MemoryMetricProvider;
import com.m2u.eyelink.agent.profiler.context.provider.ObjectBinderFactoryProvider;
import com.m2u.eyelink.agent.profiler.context.provider.PluginContextLoadResultProvider;
import com.m2u.eyelink.agent.profiler.context.provider.SamplerProvider;
import com.m2u.eyelink.agent.profiler.context.provider.ServerMetaDataHolderProvider;
import com.m2u.eyelink.agent.profiler.context.provider.StorageFactoryProvider;
import com.m2u.eyelink.agent.profiler.context.provider.TcpDataSenderProvider;
import com.m2u.eyelink.agent.profiler.context.provider.TraceContextProvider;
import com.m2u.eyelink.agent.profiler.context.provider.TraceFactoryProvider;
import com.m2u.eyelink.agent.profiler.context.provider.UdpSpanDataSenderProvider;
import com.m2u.eyelink.agent.profiler.context.provider.UdpStatDataSenderProvider;
import com.m2u.eyelink.agent.profiler.context.provider.stat.activethread.ActiveTraceMetricCollectorProvider;
import com.m2u.eyelink.agent.profiler.context.provider.stat.activethread.ActiveTraceMetricProvider;
import com.m2u.eyelink.agent.profiler.context.provider.stat.cpu.CpuLoadMetricCollectorProvider;
import com.m2u.eyelink.agent.profiler.context.provider.stat.cpu.CpuLoadMetricProvider;
import com.m2u.eyelink.agent.profiler.context.provider.stat.datasource.DataSourceMetricCollectorProvider;
import com.m2u.eyelink.agent.profiler.context.provider.stat.datasource.DataSourceMetricProvider;
import com.m2u.eyelink.agent.profiler.context.provider.stat.jvmgc.GarbageCollectorMetricProvider;
import com.m2u.eyelink.agent.profiler.context.provider.stat.jvmgc.JvmGcMetricCollectorProvider;
import com.m2u.eyelink.agent.profiler.context.provider.stat.transaction.TransactionMetricCollectorProvider;
import com.m2u.eyelink.agent.profiler.context.provider.stat.transaction.TransactionMetricProvider;
import com.m2u.eyelink.agent.profiler.context.recorder.DefaultRecorderFactory;
import com.m2u.eyelink.agent.profiler.context.recorder.RecorderFactory;
import com.m2u.eyelink.agent.profiler.instrument.InstrumentEngine;
import com.m2u.eyelink.agent.profiler.interceptor.registry.InterceptorRegistryBinder;
import com.m2u.eyelink.agent.profiler.metadata.ApiMetaDataService;
import com.m2u.eyelink.agent.profiler.metadata.DefaultStringMetaDataService;
import com.m2u.eyelink.agent.profiler.metadata.JdbcContext;
import com.m2u.eyelink.agent.profiler.metadata.SqlMetaDataService;
import com.m2u.eyelink.agent.profiler.metadata.StringMetaDataService;
import com.m2u.eyelink.agent.profiler.monitor.AgentStatMonitor;
import com.m2u.eyelink.agent.profiler.monitor.DefaultAgentStatMonitor;
import com.m2u.eyelink.agent.profiler.monitor.collector.AgentStatCollector;
import com.m2u.eyelink.agent.profiler.monitor.collector.AgentStatMetricCollector;
import com.m2u.eyelink.agent.profiler.monitor.collector.activethread.ActiveTraceMetricCollector;
import com.m2u.eyelink.agent.profiler.monitor.collector.cpu.CpuLoadMetricCollector;
import com.m2u.eyelink.agent.profiler.monitor.collector.datasource.DataSourceMetricCollector;
import com.m2u.eyelink.agent.profiler.monitor.collector.jvmgc.JvmGcMetricCollector;
import com.m2u.eyelink.agent.profiler.monitor.collector.transaction.TransactionMetricCollector;
import com.m2u.eyelink.agent.profiler.monitor.metric.activethread.ActiveTraceMetric;
import com.m2u.eyelink.agent.profiler.monitor.metric.cpu.CpuLoadMetric;
import com.m2u.eyelink.agent.profiler.monitor.metric.datasource.DataSourceMetric;
import com.m2u.eyelink.agent.profiler.monitor.metric.gc.GarbageCollectorMetric;
import com.m2u.eyelink.agent.profiler.monitor.metric.memory.MemoryMetric;
import com.m2u.eyelink.agent.profiler.monitor.metric.transaction.TransactionMetric;
import com.m2u.eyelink.agent.profiler.objectfactory.ObjectBinderFactory;
import com.m2u.eyelink.agent.profiler.plugin.PluginContextLoadResult;
import com.m2u.eyelink.agent.profiler.receiver.CommandDispatcher;
import com.m2u.eyelink.common.service.ServiceTypeRegistryService;
import com.m2u.eyelink.config.ProfilerConfig;
import com.m2u.eyelink.context.AgentInformation;
import com.m2u.eyelink.context.Sampler;
import com.m2u.eyelink.context.ServerMetaDataHolder;
import com.m2u.eyelink.context.StorageFactory;
import com.m2u.eyelink.context.TraceContext;
import com.m2u.eyelink.context.TraceFactory;
import com.m2u.eyelink.rpc.client.ELAgentClient;
import com.m2u.eyelink.rpc.client.ELAgentClientFactory;
import com.m2u.eyelink.sender.DataSender;
import com.m2u.eyelink.sender.EnhancedDataSender;
import com.m2u.eyelink.thrift.TAgentStat;
import com.m2u.eyelink.common.trace.ServiceType;

public class ApplicationContextModule extends AbstractModule {
    private final ProfilerConfig profilerConfig;
    private final ServiceTypeRegistryService serviceTypeRegistryService;
    private final AgentOption agentOption;
    private final InterceptorRegistryBinder interceptorRegistryBinder;

    public ApplicationContextModule(AgentOption agentOption, ProfilerConfig profilerConfig,
                                    ServiceTypeRegistryService serviceTypeRegistryService, InterceptorRegistryBinder interceptorRegistryBinder) {
        if (profilerConfig == null) {
            throw new NullPointerException("profilerConfig must not be null");
        }
        this.agentOption = agentOption;
        this.profilerConfig = profilerConfig;
        this.serviceTypeRegistryService = serviceTypeRegistryService;
        this.interceptorRegistryBinder = interceptorRegistryBinder;
    }

    @Override
    protected void configure() {
        binder().requireExplicitBindings();
        binder().requireAtInjectOnConstructors();
        binder().disableCircularProxies();

//        bind(ProfilerConfig.class).toInstance(profilerConfig);
        bind(ServiceTypeRegistryService.class).toInstance(serviceTypeRegistryService);
        bind(AgentOption.class).toInstance(agentOption);
        bind(Instrumentation.class).toInstance(agentOption.getInstrumentation());
        bind(InterceptorRegistryBinder.class).toInstance(interceptorRegistryBinder);

        bind(URL[].class).annotatedWith(PluginJars.class).toInstance(agentOption.getPluginJars());

        TypeLiteral<List<String>> listString = new TypeLiteral<List<String>>() {};
        bind(listString).annotatedWith(BootstrapJarPaths.class).toInstance(agentOption.getBootstrapJarPaths());

        bindAgentInformation(agentOption.getAgentId(), agentOption.getApplicationName());

        bindDataTransferComponent();

        bind(ServerMetaDataHolder.class).toProvider(ServerMetaDataHolderProvider.class).in(Scopes.SINGLETON);
        bind(StorageFactory.class).toProvider(StorageFactoryProvider.class).in(Scopes.SINGLETON);

        bindServiceComponent();

        bind(DataSourceMonitorRegistryService.class).toProvider(DataSourceMonitorRegistryServiceProvider.class).in(Scopes.SINGLETON);

        bind(IdGenerator.class).to(AtomicIdGenerator.class).in(Scopes.SINGLETON);
        bind(AsyncIdGenerator.class).to(DefaultAsyncIdGenerator.class).in(Scopes.SINGLETON);
        bind(TransactionCounter.class).to(DefaultTransactionCounter.class).in(Scopes.SINGLETON);

        bind(Sampler.class).toProvider(SamplerProvider.class).in(Scopes.SINGLETON);

        bind(TraceContext.class).toProvider(TraceContextProvider.class).in(Scopes.SINGLETON);

        bindTraceComponent();

        bind(ActiveTraceRepository.class).toProvider(ActiveTraceRepositoryProvider.class).in(Scopes.SINGLETON);

        bind(PluginContextLoadResult.class).toProvider(PluginContextLoadResultProvider.class).in(Scopes.SINGLETON);

        bind(JdbcContext.class).to(DefaultJdbcContext.class).in(Scopes.SINGLETON);
        bind(JdbcUrlParsingService.class).toProvider(JdbcUrlParsingServiceProvider.class).in(Scopes.SINGLETON);

        bind(AgentInformation.class).toProvider(AgentInformationProvider.class).in(Scopes.SINGLETON);

        bind(InstrumentEngine.class).toProvider(InstrumentEngineProvider.class).in(Scopes.SINGLETON);
        bind(ObjectBinderFactory.class).toProvider(ObjectBinderFactoryProvider.class).in(Scopes.SINGLETON);
        bind(ClassFileTransformerDispatcher.class).toProvider(ClassFileTransformerDispatcherProvider.class).in(Scopes.SINGLETON);
        bind(DynamicTransformerRegistry.class).to(DefaultDynamicTransformerRegistry.class).in(Scopes.SINGLETON);
        bind(DynamicTransformTrigger.class).toProvider(DynamicTransformTriggerProvider.class).in(Scopes.SINGLETON);
//        bind(ClassFileTransformer.class).toProvider(ClassFileTransformerWrapProvider.class).in(Scopes.SINGLETON);

        bindAgentStatComponent();

        bind(JvmInformation.class).toProvider(JvmInformationProvider.class).in(Scopes.SINGLETON);
        bind(AgentInfoSender.class).toProvider(AgentInfoSenderProvider.class).in(Scopes.SINGLETON);
        bind(AgentStatMonitor.class).to(DefaultAgentStatMonitor.class).in(Scopes.SINGLETON);
    }

    private void bindTraceComponent() {
        bind(TraceIdFactory.class).to(DefaultTraceIdFactory.class).in(Scopes.SINGLETON);
        bind(CallStackFactory.class).to(DefaultCallStackFactory.class).in(Scopes.SINGLETON);

        bind(SpanFactory.class).to(DefaultSpanFactory.class).in(Scopes.SINGLETON);
        bind(SpanChunkFactory.class).to(DefaultSpanChunkFactory.class).in(Scopes.SINGLETON);

        bind(RecorderFactory.class).to(DefaultRecorderFactory.class).in(Scopes.SINGLETON);

        bind(TraceFactory.class).toProvider(TraceFactoryProvider.class).in(Scopes.SINGLETON);
    }

    private void bindDataTransferComponent() {
        // create tcp channel

        bind(ELAgentClientFactory.class).toProvider(ELAgentClientFactoryProvider.class).in(Scopes.SINGLETON);
        bind(EnhancedDataSender.class).toProvider(TcpDataSenderProvider.class).in(Scopes.SINGLETON);
        bind(ELAgentClient.class).toProvider(ELAgentClientProvider.class).in(Scopes.SINGLETON);

        bind(CommandDispatcher.class).toProvider(CommandDispatcherProvider.class).in(Scopes.SINGLETON);

        bind(DataSender.class).annotatedWith(SpanDataSender.class)
                .toProvider(UdpSpanDataSenderProvider.class).in(Scopes.SINGLETON);
        bind(DataSender.class).annotatedWith(StatDataSender.class)
                .toProvider(UdpStatDataSenderProvider.class).in(Scopes.SINGLETON);
    }

    private void bindServiceComponent() {

        bind(StringMetaDataService.class).to(DefaultStringMetaDataService.class).in(Scopes.SINGLETON);
        bind(ApiMetaDataService.class).toProvider(ApiMetaDataServiceProvider.class).in(Scopes.SINGLETON);
        bind(SqlMetaDataService.class).to(DefaultSqlMetaDataService.class).in(Scopes.SINGLETON);
    }

    private void bindAgentInformation(String agentId, String applicationName) {

        bind(String.class).annotatedWith(AgentId.class).toInstance(agentId);
        bind(String.class).annotatedWith(ApplicationName.class).toInstance(applicationName);
        bind(Long.class).annotatedWith(AgentStartTime.class).toProvider(AgentStartTimeProvider.class).in(Scopes.SINGLETON);
        bind(ServiceType.class).annotatedWith(ApplicationServerType.class).toProvider(ApplicationServerTypeProvider.class).in(Scopes.SINGLETON);
    }

    private void bindAgentStatComponent() {
        bind(MemoryMetric.class).toProvider(MemoryMetricProvider.class).in(Scopes.SINGLETON);
        bind(GarbageCollectorMetric.class).toProvider(GarbageCollectorMetricProvider.class).in(Scopes.SINGLETON);
        bind(JvmGcMetricCollector.class).toProvider(JvmGcMetricCollectorProvider.class).in(Scopes.SINGLETON);

        bind(CpuLoadMetric.class).toProvider(CpuLoadMetricProvider.class).in(Scopes.SINGLETON);
        bind(CpuLoadMetricCollector.class).toProvider(CpuLoadMetricCollectorProvider.class).in(Scopes.SINGLETON);

        bind(TransactionMetric.class).toProvider(TransactionMetricProvider.class).in(Scopes.SINGLETON);
        bind(TransactionMetricCollector.class).toProvider(TransactionMetricCollectorProvider.class).in(Scopes.SINGLETON);

        bind(ActiveTraceMetric.class).toProvider(ActiveTraceMetricProvider.class).in(Scopes.SINGLETON);
        bind(ActiveTraceMetricCollector.class).toProvider(ActiveTraceMetricCollectorProvider.class).in(Scopes.SINGLETON);

        bind(DataSourceMetric.class).toProvider(DataSourceMetricProvider.class).in(Scopes.SINGLETON);
        bind(DataSourceMetricCollector.class).toProvider(DataSourceMetricCollectorProvider.class).in(Scopes.SINGLETON);

        bind(new TypeLiteral<AgentStatMetricCollector<TAgentStat>>() {})
                .annotatedWith(Names.named("AgentStatCollector"))
                .to(AgentStatCollector.class).in(Scopes.SINGLETON);
    }

    @Provides
    @Singleton
    public ProfilerConfig profilerConfig() {
        return profilerConfig;
    }
}