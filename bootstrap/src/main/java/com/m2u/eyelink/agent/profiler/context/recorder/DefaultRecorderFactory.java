package com.m2u.eyelink.agent.profiler.context.recorder;

import com.google.inject.Inject;
import com.m2u.eyelink.agent.profiler.context.Span;
import com.m2u.eyelink.agent.profiler.metadata.SqlMetaDataService;
import com.m2u.eyelink.agent.profiler.metadata.StringMetaDataService;
import com.m2u.eyelink.context.DefaultSpanRecorder;
import com.m2u.eyelink.context.SpanRecorder;
import com.m2u.eyelink.context.WrappedSpanEventRecorder;

public class DefaultRecorderFactory implements RecorderFactory {

    private final StringMetaDataService stringMetaDataService;
    private final SqlMetaDataService sqlMetaDataService;

    @Inject
    public DefaultRecorderFactory(StringMetaDataService stringMetaDataService, SqlMetaDataService sqlMetaDataService) {
        if (stringMetaDataService == null) {
            throw new NullPointerException("stringMetaDataService must not be null");
        }
        if (sqlMetaDataService == null) {
            throw new NullPointerException("sqlMetaDataService must not be null");
        }
        this.stringMetaDataService = stringMetaDataService;
        this.sqlMetaDataService = sqlMetaDataService;
    }

    @Override
    public SpanRecorder newSpanRecorder(Span span, boolean isRoot, boolean sampling) {
        return new DefaultSpanRecorder(span, isRoot, sampling, stringMetaDataService, sqlMetaDataService);
    }

    @Override
    public WrappedSpanEventRecorder newWrappedSpanEventRecorder() {
        return new WrappedSpanEventRecorder(stringMetaDataService, sqlMetaDataService);
    }
}
