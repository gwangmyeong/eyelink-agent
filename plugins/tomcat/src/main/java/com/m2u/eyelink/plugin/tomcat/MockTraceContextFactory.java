package com.m2u.eyelink.plugin.tomcat;

import com.google.inject.util.Providers;
import com.m2u.eyelink.agent.profiler.context.CallStackFactory;
import com.m2u.eyelink.agent.profiler.context.DefaultCallStackFactory;
import com.m2u.eyelink.agent.profiler.context.DefaultSpanFactory;
import com.m2u.eyelink.agent.profiler.context.SpanFactory;
import com.m2u.eyelink.agent.profiler.context.active.ActiveTraceRepository;
import com.m2u.eyelink.agent.profiler.context.active.DefaultActiveTraceRepository;
import com.m2u.eyelink.agent.profiler.context.id.AsyncIdGenerator;
import com.m2u.eyelink.agent.profiler.context.id.AtomicIdGenerator;
import com.m2u.eyelink.agent.profiler.context.id.DefaultAsyncIdGenerator;
import com.m2u.eyelink.agent.profiler.context.id.DefaultTraceIdFactory;
import com.m2u.eyelink.agent.profiler.context.id.IdGenerator;
import com.m2u.eyelink.agent.profiler.context.id.TraceIdFactory;
import com.m2u.eyelink.agent.profiler.context.monitor.DisabledJdbcContext;
import com.m2u.eyelink.agent.profiler.context.provider.DefaultSqlMetaDataService;
import com.m2u.eyelink.agent.profiler.context.provider.TraceFactoryProvider;
import com.m2u.eyelink.agent.profiler.context.recorder.DefaultRecorderFactory;
import com.m2u.eyelink.agent.profiler.context.recorder.RecorderFactory;
import com.m2u.eyelink.agent.profiler.metadata.ApiMetaDataService;
import com.m2u.eyelink.agent.profiler.metadata.DefaultApiMetaDataService;
import com.m2u.eyelink.agent.profiler.metadata.DefaultStringMetaDataService;
import com.m2u.eyelink.agent.profiler.metadata.SqlMetaDataService;
import com.m2u.eyelink.agent.profiler.metadata.StringMetaDataService;
import com.m2u.eyelink.agent.profiler.sampler.SamplerFactory;
import com.m2u.eyelink.common.trace.ServiceType;
import com.m2u.eyelink.config.ProfilerConfig;
import com.m2u.eyelink.context.AgentInformation;
import com.m2u.eyelink.context.DefaultServerMetaDataHolder;
import com.m2u.eyelink.context.DefaultTraceContext;
import com.m2u.eyelink.context.LogStorageFactory;
import com.m2u.eyelink.context.RuntimeMXBeanUtils;
import com.m2u.eyelink.context.Sampler;
import com.m2u.eyelink.context.ServerMetaDataHolder;
import com.m2u.eyelink.context.StorageFactory;
import com.m2u.eyelink.context.TraceContext;
import com.m2u.eyelink.context.TraceFactory;
import com.m2u.eyelink.sender.EnhancedDataSender;
import com.m2u.eyelink.sender.LoggingDataSender;
import com.m2u.eyelink.test.TestAgentInformation;

public class MockTraceContextFactory {

    private static final boolean TRACE_ACTIVE_THREAD = true;
    private static final boolean TRACE_DATASOURCE = false;

    private final AgentInformation agentInformation;

    private final StorageFactory storageFactory;

    private final AtomicIdGenerator idGenerator;
    private final Sampler sampler;
    private final ActiveTraceRepository activeTraceRepository;

    private final ServerMetaDataHolder serverMetaDataHolder;

    private final EnhancedDataSender enhancedDataSender;

    private final ApiMetaDataService apiMetaDataService;
    private final StringMetaDataService stringMetaDataService;
    private final SqlMetaDataService sqlMetaDataService;

    private final TraceContext traceContext;

    public static TraceContext newTestTraceContext(ProfilerConfig profilerConfig) {
        MockTraceContextFactory mockTraceContextFactory = newTestTraceContextFactory(profilerConfig);
        return mockTraceContextFactory.getTraceContext();
    }

    public static MockTraceContextFactory newTestTraceContextFactory(ProfilerConfig profilerConfig) {

        return new MockTraceContextFactory(profilerConfig);
    }


    public MockTraceContextFactory(ProfilerConfig profilerConfig) {
        this.agentInformation = new TestAgentInformation();

        this.storageFactory = new LogStorageFactory();

        final SamplerFactory samplerFactory = new SamplerFactory();
        this.sampler = createSampler(profilerConfig, samplerFactory);

        this.idGenerator = new AtomicIdGenerator();
        this.activeTraceRepository = newActiveTraceRepository();

        final AsyncIdGenerator asyncIdGenerator = new DefaultAsyncIdGenerator();
        this.serverMetaDataHolder = new DefaultServerMetaDataHolder(RuntimeMXBeanUtils.getVmArgs());

        final String applicationName = agentInformation.getAgentId();
        final String agentId = agentInformation.getAgentId();
        final long agentStartTime = agentInformation.getStartTime();
        final ServiceType agentServiceType = agentInformation.getServerType();
        this.enhancedDataSender = new LoggingDataSender();

        this.apiMetaDataService = new DefaultApiMetaDataService(agentId, agentStartTime, enhancedDataSender);
        this.stringMetaDataService = new DefaultStringMetaDataService(agentId, agentStartTime, enhancedDataSender);

        final int jdbcSqlCacheSize = profilerConfig.getJdbcSqlCacheSize();
        this.sqlMetaDataService = new DefaultSqlMetaDataService(agentId, agentStartTime, enhancedDataSender, jdbcSqlCacheSize);


        CallStackFactory callStackFactory = new DefaultCallStackFactory(64);
        TraceIdFactory traceIdFactory = new DefaultTraceIdFactory(agentId, agentStartTime, idGenerator);
        SpanFactory spanFactory = new DefaultSpanFactory(applicationName, agentId, agentStartTime, agentServiceType);

        RecorderFactory recorderFactory = new DefaultRecorderFactory(stringMetaDataService, sqlMetaDataService);

        final TraceFactoryProvider traceFactoryBuilder = new TraceFactoryProvider(callStackFactory, storageFactory, sampler, idGenerator, traceIdFactory, asyncIdGenerator,
                Providers.of(activeTraceRepository), spanFactory, recorderFactory);
        TraceFactory traceFactory = traceFactoryBuilder.get();
        this.traceContext = new DefaultTraceContext(profilerConfig, agentInformation,
                traceIdFactory, traceFactory, asyncIdGenerator, serverMetaDataHolder,
                apiMetaDataService, stringMetaDataService, sqlMetaDataService,
                DisabledJdbcContext.INSTANCE
        );
    }

    private Sampler createSampler(ProfilerConfig profilerConfig, SamplerFactory samplerFactory) {
        boolean samplingEnable = profilerConfig.isSamplingEnable();
        int samplingRate = profilerConfig.getSamplingRate();
        return samplerFactory.createSampler(samplingEnable, samplingRate);
    }


    private static ActiveTraceRepository newActiveTraceRepository() {
        if (TRACE_ACTIVE_THREAD) {
            return new DefaultActiveTraceRepository();
        }
        return null;
    }

    public AgentInformation getAgentInformation() {
        return agentInformation;
    }

    public StorageFactory getStorageFactory() {
        return storageFactory;
    }

    public IdGenerator getIdGenerator() {
        return idGenerator;
    }

    public Sampler getSampler() {
        return sampler;
    }

    public ActiveTraceRepository getActiveTraceRepository() {
        return activeTraceRepository;
    }


    public ServerMetaDataHolder getServerMetaDataHolder() {
        return serverMetaDataHolder;
    }

    public EnhancedDataSender getEnhancedDataSender() {
        return enhancedDataSender;
    }

    public ApiMetaDataService getApiMetaDataCacheService() {
        return apiMetaDataService;
    }

    public StringMetaDataService getStringMetaDataCacheService() {
        return stringMetaDataService;
    }

    public SqlMetaDataService getSqlMetaDataCacheService() {
        return sqlMetaDataService;
    }

    public TraceContext getTraceContext() {
        return traceContext;
    }
}
