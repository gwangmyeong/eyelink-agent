package com.m2u.eyelink.agent.profiler.metadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.context.CachingSqlNormalizer;
import com.m2u.eyelink.context.DefaultParsingResult;
import com.m2u.eyelink.context.DefaultSqlParser;
import com.m2u.eyelink.context.ParsingResult;
import com.m2u.eyelink.context.ParsingResultInternal;
import com.m2u.eyelink.util.NormalizedSql;
import com.m2u.eyelink.util.SqlParser;

public class DefaultCachingSqlNormalizer implements CachingSqlNormalizer {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final DefaultParsingResult EMPTY_OBJECT = new DefaultParsingResult("");

    private final SimpleCache<String> sqlCache;
    private final SqlParser sqlParser;

    public DefaultCachingSqlNormalizer(int cacheSize) {
        this.sqlCache = new SimpleCache<String>(cacheSize);
        this.sqlParser = new DefaultSqlParser();
    }

    @Override
    public ParsingResult wrapSql(String sql) {
        if (sql == null) {
            return EMPTY_OBJECT;
        }
        return new DefaultParsingResult(sql);
    }

    @Override
    public boolean normalizedSql(ParsingResult parsingResult) {
        if (parsingResult == null) {
            return false;
        }
        if (parsingResult == EMPTY_OBJECT) {
            return false;
        }
        if (parsingResult.getId() != ParsingResult.ID_NOT_EXIST) {
            // already cached
            return false;
        }

        if (!(parsingResult instanceof ParsingResultInternal)) {
            if (logger.isWarnEnabled()) {
                logger.warn("unsupported ParsingResult Type type {}", parsingResult);
            }
            throw new IllegalArgumentException("unsupported ParsingResult Type");
        }

        final ParsingResultInternal parsingResultInternal = (ParsingResultInternal) parsingResult;

        final String originalSql = parsingResultInternal.getOriginalSql();
        final NormalizedSql normalizedSql = this.sqlParser.normalizedSql(originalSql);

        final Result cachingResult = this.sqlCache.put(normalizedSql.getNormalizedSql());

        // set normalizedSql
        // set sqlId
        final boolean success = parsingResultInternal.setId(cachingResult.getId());
        if (!success) {
            if (logger.isWarnEnabled()) {
                logger.warn("invalid state. setSqlId fail setId:{}, ParsingResultInternal:{}", cachingResult.getId(), parsingResultInternal);
            }
        }

        parsingResultInternal.setSql(normalizedSql.getNormalizedSql());
        parsingResultInternal.setOutput(normalizedSql.getParseParameter());

        return cachingResult.isNewValue();
    }


}
