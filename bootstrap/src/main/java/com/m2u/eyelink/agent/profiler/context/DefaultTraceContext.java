package com.m2u.eyelink.agent.profiler.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.agent.profiler.context.id.AsyncIdGenerator;
import com.m2u.eyelink.agent.profiler.context.id.TraceIdFactory;
import com.m2u.eyelink.agent.profiler.metadata.ApiMetaDataService;
import com.m2u.eyelink.agent.profiler.metadata.JdbcContext;
import com.m2u.eyelink.agent.profiler.metadata.SqlMetaDataService;
import com.m2u.eyelink.agent.profiler.metadata.StringMetaDataService;
import com.m2u.eyelink.annotations.InterfaceAudience;
import com.m2u.eyelink.config.ProfilerConfig;
import com.m2u.eyelink.context.AgentInformation;
import com.m2u.eyelink.context.AsyncTraceId;
import com.m2u.eyelink.context.MethodDescriptor;
import com.m2u.eyelink.context.ParsingResult;
import com.m2u.eyelink.context.ServerMetaDataHolder;
import com.m2u.eyelink.context.Trace;
import com.m2u.eyelink.context.TraceContext;
import com.m2u.eyelink.context.TraceFactory;
import com.m2u.eyelink.context.TraceId;

public class DefaultTraceContext implements TraceContext {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final TraceIdFactory traceIdFactory;
    private final TraceFactory traceFactory;

    private final AgentInformation agentInformation;

    private final ApiMetaDataService apiMetaDataService;
    private final StringMetaDataService stringMetaDataService;
    private final SqlMetaDataService sqlMetaDataService;

    private final ProfilerConfig profilerConfig;

    private final ServerMetaDataHolder serverMetaDataHolder;

    private final JdbcContext jdbcContext;

    private final AsyncIdGenerator asyncIdGenerator;

    public DefaultTraceContext(ProfilerConfig profilerConfig, final AgentInformation agentInformation,
                               TraceIdFactory traceIdFactory,
                               TraceFactory traceFactory,
                               AsyncIdGenerator asyncIdGenerator,
                               ServerMetaDataHolder serverMetaDataHolder,
                               ApiMetaDataService apiMetaDataService,
                               StringMetaDataService stringMetaDataService,
                               SqlMetaDataService sqlMetaDataService,
                               JdbcContext jdbcContext
    ) {
        if (profilerConfig == null) {
            throw new NullPointerException("profilerConfig must not be null");
        }
        if (agentInformation == null) {
            throw new NullPointerException("agentInformation must not be null");
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

        if (apiMetaDataService == null) {
            throw new NullPointerException("apiMetaDataService must not be null");
        }
        if (stringMetaDataService == null) {
            throw new NullPointerException("stringMetaDataService must not be null");
        }
        if (sqlMetaDataService == null) {
            throw new NullPointerException("sqlMetaDataService must not be null");
        }
        this.profilerConfig = profilerConfig;
        this.agentInformation = agentInformation;
        this.serverMetaDataHolder = serverMetaDataHolder;

        this.traceIdFactory = traceIdFactory;
        this.traceFactory = traceFactory;
        this.asyncIdGenerator = asyncIdGenerator;
        this.jdbcContext = jdbcContext;

        this.apiMetaDataService = apiMetaDataService;
        this.stringMetaDataService = stringMetaDataService;
        this.sqlMetaDataService = sqlMetaDataService;
    }

    /**
     * Return trace only if current transaction can be sampled.
     *
     * @return
     */
    public Trace currentTraceObject() {
        return traceFactory.currentTraceObject();
    }

    public Trace currentRpcTraceObject() {
        return traceFactory.currentTraceObject();
    }

    /**
     * Return trace without sampling check.
     *
     * @return
     */
    @Override
    public Trace currentRawTraceObject() {
        return traceFactory.currentRawTraceObject();
    }

    @Override
    public Trace disableSampling() {
        // return null; is bug. #93
        return traceFactory.disableSampling();
    }


    @Override
    public ProfilerConfig getProfilerConfig() {
        return profilerConfig;
    }

    @Override
    public Trace continueTraceObject(final TraceId traceId) {
        return traceFactory.continueTraceObject(traceId);
    }


    @Override
    public Trace continueTraceObject(Trace trace) {
        return traceFactory.continueTraceObject(trace);
    }


    @Override
    public Trace newTraceObject() {
        return traceFactory.newTraceObject();
    }

    @InterfaceAudience.LimitedPrivate("vert.x")
    @Override
    public Trace newAsyncTraceObject() {
        return traceFactory.newAsyncTraceObject();
    }

    @InterfaceAudience.LimitedPrivate("vert.x")
    @Override
    public Trace continueAsyncTraceObject(final TraceId traceId) {
        return traceFactory.continueAsyncTraceObject(traceId);
    }

    @Override
    public Trace continueAsyncTraceObject(AsyncTraceId traceId, int asyncId, long startTime) {
        return traceFactory.continueAsyncTraceObject(traceId, asyncId, startTime);
    }

    @Override
    public Trace removeTraceObject() {
        return traceFactory.removeTraceObject();
    }

    public AgentInformation getAgentInformation() {
        return agentInformation;
    }

    @Override
    public String getAgentId() {
        return this.agentInformation.getAgentId();
    }

    @Override
    public String getApplicationName() {
        return this.agentInformation.getApplicationName();
    }

    @Override
    public long getAgentStartTime() {
        return this.agentInformation.getStartTime();
    }

    @Override
    public short getServerTypeCode() {
        return this.agentInformation.getServerType().getCode();
    }

    @Override
    public String getServerType() {
        return this.agentInformation.getServerType().getDesc();
    }

    @Override
    public int cacheApi(final MethodDescriptor methodDescriptor) {
        return this.apiMetaDataService.cacheApi(methodDescriptor);
    }

    @Override
    public int cacheString(final String value) {
        return this.stringMetaDataService.cacheString(value);
    }

    @Override
    public TraceId createTraceId(final String transactionId, final long parentSpanID, final long spanId, final short flags) {
        if (transactionId == null) {
            throw new NullPointerException("transactionId must not be null");
        }
        // TODO Should handle exception when parsing failed.
        return traceIdFactory.parse(transactionId, parentSpanID, spanId, flags);
    }

    @Override
    public ParsingResult parseSql(final String sql) {
        return this.sqlMetaDataService.parseSql(sql);
    }

    @Override
    public boolean cacheSql(ParsingResult parsingResult) {
        return this.sqlMetaDataService.cacheSql(parsingResult);
    }

    @Override
    public ServerMetaDataHolder getServerMetaDataHolder() {
        return this.serverMetaDataHolder;
    }

    @Override
    public int getAsyncId() {
        return this.asyncIdGenerator.nextAsyncId();
    }

    @Override
    public JdbcContext getJdbcContext() {
        return jdbcContext;
    }

}
