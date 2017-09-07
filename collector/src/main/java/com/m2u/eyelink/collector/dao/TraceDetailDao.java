package com.m2u.eyelink.collector.dao;

import com.m2u.eyelink.collector.bo.SpanBo;

public interface TraceDetailDao {
    void insert(SpanBo span);
}
