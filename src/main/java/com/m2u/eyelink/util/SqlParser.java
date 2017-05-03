package com.m2u.eyelink.util;

import java.util.List;


public interface SqlParser {
    NormalizedSql normalizedSql(String sql);

    String combineOutputParams(String sql, List<String> outputParams);

    String combineBindValues(String sql, List<String> bindValues);
}
