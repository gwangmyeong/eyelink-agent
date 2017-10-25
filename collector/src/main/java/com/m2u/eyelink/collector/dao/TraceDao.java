package com.m2u.eyelink.collector.dao;

import com.m2u.eyelink.collector.bo.SpanBo;

public interface TraceDao {
    void insert(SpanBo span);

    void insertSpanChunk(SpanChunkBo spanChunk);
}
