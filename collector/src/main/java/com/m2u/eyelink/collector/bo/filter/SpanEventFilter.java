package com.m2u.eyelink.collector.bo.filter;

import com.m2u.eyelink.collector.bo.SpanEventBo;

public interface SpanEventFilter {
    boolean ACCEPT = true;
    boolean REJECT = false;

    boolean filter(SpanEventBo spanEventBo);
}
