package com.m2u.eyelink.agent.profiler.metadata;

import com.m2u.eyelink.context.ParsingResult;

public interface SqlMetaDataService {

    ParsingResult parseSql(final String sql);

    boolean cacheSql(ParsingResult parsingResult);
}
