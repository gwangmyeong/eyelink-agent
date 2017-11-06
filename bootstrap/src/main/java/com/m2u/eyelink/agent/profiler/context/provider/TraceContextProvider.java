package com.m2u.eyelink.agent.profiler.context.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.m2u.eyelink.agent.profiler.context.DefaultTraceContext;
import com.m2u.eyelink.agent.profiler.context.id.AsyncIdGenerator;
import com.m2u.eyelink.agent.profiler.context.id.TraceIdFactory;
import com.m2u.eyelink.agent.profiler.metadata.ApiMetaDataService;
import com.m2u.eyelink.agent.profiler.metadata.JdbcContext;
import com.m2u.eyelink.agent.profiler.metadata.SqlMetaDataService;
import com.m2u.eyelink.agent.profiler.metadata.StringMetaDataService;
import com.m2u.eyelink.config.ProfilerConfig;
import com.m2u.eyelink.context.AgentInformation;
import com.m2u.eyelink.context.ServerMetaDataHolder;
import com.m2u.eyelink.context.TraceContext;
import com.m2u.eyelink.context.TraceFactory;

public class TraceContextProvider implements Provider<TraceContext> {
    private final ProfilerConfig profilerConfig;
    private final Provider<AgentInformation> agentInformationProvider;

    private final TraceIdFactory traceIdFactory;
    private final TraceFactory traceFactory;

    private final ServerMetaDataHolder serverMetaDataHolder;
    private final ApiMetaDataService apiMetaDataService;
    private final StringMetaDataService stringMetaDataService;
    private final SqlMetaDataService sqlMetaDataService;
    private final JdbcContext jdbcContext;
    private final AsyncIdGenerator asyncIdGenerator;

    @Inject
    public TraceContextProvider(ProfilerConfig profilerConfig,
                                final Provider<AgentInformation> agentInformationProvider,
                                TraceIdFactory traceIdFactory,
                                TraceFactory traceFactory,
                                AsyncIdGenerator asyncIdGenerator,
                                ServerMetaDataHolder serverMetaDataHolder,
                                ApiMetaDataService apiMetaDataService,
                                StringMetaDataService stringMetaDataService,
                                SqlMetaDataService sqlMetaDataService,
                                JdbcContext jdbcContext) {
        if (profilerConfig == null) {
            throw new NullPointerException("profilerConfig must not be null");
        }
        if (agentInformationProvider == null) {
            throw new NullPointerException("agentInformationProvider must not be null");
        }
        if (traceIdFactory == null) {
            throw new NullPointerException("traceIdFactory must not be null");
        }
        if (traceFactory == null) {
            throw new NullPointerException("traceFactory must not be null");
        }
        if (asyncIdGenerator == null) {
            throw new NullPointerException("asyncIdGenerator must not be null");
        }
        if (serverMetaDataHolder == null) {
            throw new NullPointerException("serverMetaDataHolder must not be null");
        }
        if (apiMetaDataService == null) {
            throw new NullPointerException("apiMetaDataService must not be null");
        }
        if (stringMetaDataService == null) {
            throw new NullPointerException("stringMetaDataService must not be null");
        }
        if (sqlMetaDataService == null) {
            throw new NullPointerException("sqlMetaDataService must not be null");
        }
        if (jdbcContext == null) {
            throw new NullPointerException("jdbcContext must not be null");
        }
        this.profilerConfig = profilerConfig;
        this.agentInformationProvider = agentInformationProvider;

        this.traceIdFactory = traceIdFactory;
        this.traceFactory = traceFactory;
        this.asyncIdGenerator = asyncIdGenerator;
        this.serverMetaDataHolder = serverMetaDataHolder;
        this.apiMetaDataService = apiMetaDataService;
        this.stringMetaDataService = stringMetaDataService;
        this.sqlMetaDataService = sqlMetaDataService;
        this.jdbcContext = jdbcContext;
    }


    @Override
    public TraceContext get() {
        AgentInformation agentInformation = this.agentInformationProvider.get();
        return new DefaultTraceContext(profilerConfig, agentInformation, traceIdFactory, traceFactory, asyncIdGenerator,
                serverMetaDataHolder, apiMetaDataService, stringMetaDataService, sqlMetaDataService, jdbcContext);
    }
}
