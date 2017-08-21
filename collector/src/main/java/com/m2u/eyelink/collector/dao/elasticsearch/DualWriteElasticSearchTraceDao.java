package com.m2u.eyelink.collector.dao.elasticsearch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.collector.bo.SpanBo;
import com.m2u.eyelink.collector.dao.SpanChunkBo;
import com.m2u.eyelink.collector.dao.TraceDao;

public class DualWriteElasticSearchTraceDao implements TraceDao {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final TraceDao master;
    private final TraceDao slave;

    public DualWriteElasticSearchTraceDao(TraceDao master, TraceDao slave) {
        if (master == null) {
            throw new NullPointerException("master must not be null");
        }
        if (slave == null) {
            throw new NullPointerException("slave must not be null");
        }
        this.master = master;
        this.slave = slave;
    }

    @Override
    public void insert(SpanBo span) {
        Throwable masterException = null;
        try {
            master.insert(span);
        } catch (Throwable e) {
            masterException = e;
        }
        try {
            slave.insert(span);
        } catch (Throwable e) {
            logger.warn("slave insert(TSpan) Error:{}", e.getMessage(), e);
        }
        rethrowRuntimeException(masterException);
    }

    @Override
    public void insertSpanChunk(SpanChunkBo spanChunkBo) {
        Throwable masterException = null;
        try {
            master.insertSpanChunk(spanChunkBo);
        } catch (Throwable e) {
            masterException = e;
        }
        try {
            slave.insertSpanChunk(spanChunkBo);
        } catch (Throwable e) {
            logger.warn("slave insertSpanChunk(TSpanChunk) Error:{}", e.getMessage(), e);
        }
        rethrowRuntimeException(masterException);
    }

    private void rethrowRuntimeException(Throwable exception) {
        if (exception != null) {
            this.<RuntimeException>rethrowException(exception);
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends Exception> void rethrowException(final Throwable exception) throws T {
        throw (T) exception;
    }
}
