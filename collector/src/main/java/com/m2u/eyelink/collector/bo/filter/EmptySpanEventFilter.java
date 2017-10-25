package com.m2u.eyelink.collector.bo.filter;

import com.m2u.eyelink.collector.bo.SpanEventBo;

public class EmptySpanEventFilter implements SpanEventFilter {

    public EmptySpanEventFilter() {
    }

    @Override
    public boolean filter(SpanEventBo spanEventBo) {
        return ACCEPT;
    }
}
