package com.m2u.eyelink.collector.handler;

import java.util.List;

import org.apache.thrift.TBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.m2u.eyelink.collector.bo.SpanEventBo;
import com.m2u.eyelink.collector.bo.SpanFactory;
import com.m2u.eyelink.collector.dao.SpanChunkBo;
import com.m2u.eyelink.collector.dao.TraceDao;
import com.m2u.eyelink.common.service.ServiceTypeRegistryService;
import com.m2u.eyelink.context.thrift.TSpanChunk;
import com.m2u.eyelink.trace.ServiceType;

@Service
public class SpanChunkHandler implements SimpleHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("elasticSearchTraceDaoFactory")
    private TraceDao traceDao;

    @Autowired
    private StatisticsHandler statisticsHandler;

    @Autowired
    private ServiceTypeRegistryService registry;

    @Autowired
    private SpanFactory spanFactory;

    @Override
    public void handleSimple(TBase<?, ?> tbase) {

        try {
            final SpanChunkBo spanChunkBo = newSpanChunkBo(tbase);

            traceDao.insertSpanChunk(spanChunkBo);

            final ServiceType applicationServiceType = getApplicationServiceType(spanChunkBo);
            List<SpanEventBo> spanEventList = spanChunkBo.getSpanEventBoList();
            if (spanEventList != null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("SpanChunk Size:{}", spanEventList.size());
                }
                // TODO need to batch update later.
                for (SpanEventBo spanEvent : spanEventList) {
                    final ServiceType spanEventType = registry.findServiceType(spanEvent.getServiceType());

                    if (!spanEventType.isRecordStatistics()) {
                        continue;
                    }

                    // if terminal update statistics
                    final int elapsed = spanEvent.getEndElapsed();
                    final boolean hasException = spanEvent.hasException();

                    /*
                     * save information to draw a server map based on statistics
                     */
                    // save the information of caller (the spanevent that span called)
                    statisticsHandler.updateCaller(spanChunkBo.getApplicationId(), applicationServiceType, spanChunkBo.getAgentId(), spanEvent.getDestinationId(), spanEventType, spanEvent.getEndPoint(), elapsed, hasException);

                    // save the information of callee (the span that called spanevent)
                    statisticsHandler.updateCallee(spanEvent.getDestinationId(), spanEventType, spanChunkBo.getApplicationId(), applicationServiceType, spanChunkBo.getEndPoint(), elapsed, hasException);
                }
            }
        } catch (Exception e) {
            logger.warn("SpanChunk handle error Caused:{}", e.getMessage(), e);
        }
    }

    private SpanChunkBo newSpanChunkBo(TBase<?, ?> tbase) {
        if (!(tbase instanceof TSpanChunk)) {
            throw new IllegalArgumentException("unexpected tbase:" + tbase + " expected:" + this.getClass().getName());
        }

        final TSpanChunk tSpanChunk = (TSpanChunk) tbase;
        if (logger.isDebugEnabled()) {
            logger.debug("Received SpanChunk={}", tbase);
        }

        return this.spanFactory.buildSpanChunkBo(tSpanChunk);
    }

    private ServiceType getApplicationServiceType(SpanChunkBo spanChunk) {
        final short applicationServiceTypeCode = spanChunk.getApplicationServiceType();
        return registry.findServiceType(applicationServiceTypeCode);
    }
}