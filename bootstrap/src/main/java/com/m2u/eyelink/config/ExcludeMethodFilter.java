package com.m2u.eyelink.config;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.m2u.eyelink.util.StringUtils;

public class ExcludeMethodFilter implements Filter<String> {
    private final Set<String> excludeMethods;

    public ExcludeMethodFilter(String excludeFormat) {
        this(excludeFormat, ",");
    }

    public ExcludeMethodFilter(String excludeFormat, String separator) {
        if (StringUtils.isEmpty(separator)) {
            this.excludeMethods = Collections.emptySet();
            return;
        }
        final List<String> splitList = StringUtils.splitAndTrim(excludeFormat, separator);
        this.excludeMethods = new HashSet<String>();
        for (String method : splitList) {
            this.excludeMethods.add(method.toUpperCase());
        }
    }

    @Override
    public boolean filter(String method) {
        return excludeMethods.contains(method);
    }

}
