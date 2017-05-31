package com.m2u.eyelink.agent.profiler.context.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.m2u.eyelink.agent.profiler.context.module.AgentId;
import com.m2u.eyelink.agent.profiler.context.module.AgentStartTime;
import com.m2u.eyelink.agent.profiler.metadata.SqlMetaDataService;
import com.m2u.eyelink.config.ProfilerConfig;
import com.m2u.eyelink.context.CachingSqlNormalizer;
import com.m2u.eyelink.context.DefaultCachingSqlNormalizer;
import com.m2u.eyelink.context.ParsingResult;
import com.m2u.eyelink.context.TSqlMetaData;
import com.m2u.eyelink.sender.EnhancedDataSender;

public class DefaultSqlMetaDataService implements SqlMetaDataService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final boolean isDebug = logger.isDebugEnabled();

    private final CachingSqlNormalizer cachingSqlNormalizer;

    private final String agentId;
    private final long agentStartTime;
    private final EnhancedDataSender enhancedDataSender;

    @Inject
    public DefaultSqlMetaDataService(ProfilerConfig profilerConfig, @AgentId String agentId,
                                     @AgentStartTime long agentStartTime, EnhancedDataSender enhancedDataSender) {
        this(agentId, agentStartTime, enhancedDataSender, profilerConfig.getJdbcSqlCacheSize());
    }

    public DefaultSqlMetaDataService(String agentId, long agentStartTime, EnhancedDataSender enhancedDataSender, int jdbcSqlCacheSize) {
        if (agentId == null) {
            throw new NullPointerException("agentId must not be null");
        }
        if (enhancedDataSender == null) {
            throw new NullPointerException("enhancedDataSender must not be null");
        }
        this.agentId = agentId;
        this.agentStartTime = agentStartTime;
        this.enhancedDataSender = enhancedDataSender;
        this.cachingSqlNormalizer = new DefaultCachingSqlNormalizer(jdbcSqlCacheSize);
    }

    @Override
    public ParsingResult parseSql(final String sql) {
        // lazy sql normalization
        return this.cachingSqlNormalizer.wrapSql(sql);
    }


    @Override
    public boolean cacheSql(ParsingResult parsingResult) {

        if (parsingResult == null) {
            return false;
        }
        // lazy sql parsing
        boolean isNewValue = this.cachingSqlNormalizer.normalizedSql(parsingResult);
        if (isNewValue) {
            if (isDebug) {
                // TODO logging hit ratio could help debugging
                logger.debug("NewSQLParsingResult:{}", parsingResult);
            }

            // isNewValue means that the value is newly cached.
            // So the sql could be new one. We have to send sql metadata to collector.
            final TSqlMetaData sqlMetaData = new TSqlMetaData();
            sqlMetaData.setAgentId(agentId);
            sqlMetaData.setAgentStartTime(agentStartTime);

            sqlMetaData.setSqlId(parsingResult.getId());
            sqlMetaData.setSql(parsingResult.getSql());

            this.enhancedDataSender.request(sqlMetaData);
        }
        return isNewValue;
    }

}
