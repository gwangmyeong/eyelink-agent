package com.m2u.eyelink.collector.bo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.m2u.eyelink.collector.bo.filter.EmptySpanEventFilter;
import com.m2u.eyelink.collector.bo.filter.SpanEventFilter;
import com.m2u.eyelink.collector.dao.SpanChunkBo;
import com.m2u.eyelink.collector.server.util.AcceptedTimeService;
import com.m2u.eyelink.collector.server.util.EmptyAcceptedTimeService;
import com.m2u.eyelink.collector.util.AnnotationTranscoder;
import com.m2u.eyelink.common.service.ServiceTypeRegistryService;
import com.m2u.eyelink.context.TIntStringValue;
import com.m2u.eyelink.context.TSpanEvent;
import com.m2u.eyelink.context.thrift.TAnnotation;
import com.m2u.eyelink.context.thrift.TSpan;
import com.m2u.eyelink.context.thrift.TSpanChunk;
import com.m2u.eyelink.util.CollectionUtils;
import com.m2u.eyelink.util.TransactionId;
import com.m2u.eyelink.util.TransactionIdUtils;

@Component
public class SpanFactory {

	@Autowired
	private ServiceTypeRegistryService registry;

    private SpanEventFilter spanEventFilter = new EmptySpanEventFilter();

    private AcceptedTimeService acceptedTimeService = new EmptyAcceptedTimeService();

    private static final AnnotationTranscoder transcoder = new AnnotationTranscoder();

    public SpanFactory() {
    }

    @Autowired(required = false)
    public void setSpanEventFilter(SpanEventFilter spanEventFilter) {
        this.spanEventFilter = spanEventFilter;
    }

    @Autowired(required = false)
    public void setAcceptedTimeService(AcceptedTimeService acceptedTimeService) {
        this.acceptedTimeService = acceptedTimeService;
    }

    public SpanBo buildSpanBo(TSpan tSpan) {

        final SpanBo spanBo = newSpanBo(tSpan);

        List<TSpanEvent> spanEventList = tSpan.getSpanEventList();
        List<SpanEventBo> spanEventBoList = buildSpanEventBoList(spanEventList);
        spanBo.addSpanEventBoList(spanEventBoList);

        long acceptedTime = acceptedTimeService.getAcceptedTime();
        spanBo.setCollectorAcceptTime(acceptedTime);

        return spanBo;
    }

    // for test
    SpanBo newSpanBo(TSpan tSpan) {
        final SpanBo spanBo = new SpanBo();
        spanBo.setAgentId(tSpan.getAgentId());
        spanBo.setApplicationId(tSpan.getApplicationName());
        spanBo.setAgentStartTime(tSpan.getAgentStartTime());

        final TransactionId transactionId = newTransactionId(tSpan.getTransactionId(), spanBo);
        spanBo.setTransactionId(transactionId);

        spanBo.setSpanId(tSpan.getSpanId());
        spanBo.setParentSpanId(tSpan.getParentSpanId());

        spanBo.setStartTime(tSpan.getStartTime());
        spanBo.setElapsed(tSpan.getElapsed());

        spanBo.setRpc(tSpan.getRpc());

        spanBo.setServiceType(tSpan.getServiceType());
        spanBo.setServiceTypeName(registry.findServiceType(tSpan.getServiceType()).getName());
        spanBo.setEndPoint(tSpan.getEndPoint());
        spanBo.setFlag(tSpan.getFlag());
        spanBo.setApiId(tSpan.getApiId());

        spanBo.setErrCode(tSpan.getErr());

        spanBo.setAcceptorHost(tSpan.getAcceptorHost());
        spanBo.setRemoteAddr(tSpan.getRemoteAddr());

        spanBo.setLoggingTransactionInfo(tSpan.getLoggingTransactionInfo());

        // FIXME (2015.03) Legacy - applicationServiceType added in v1.1.0
        // applicationServiceType is not saved for older versions where applicationServiceType does not exist.
        if (tSpan.isSetApplicationServiceType()) {
            spanBo.setApplicationServiceType(tSpan.getApplicationServiceType());
            spanBo.setApplicationServiceTypeName(registry.findServiceType(tSpan.getApplicationServiceType()).getName());
        } else {
            spanBo.setApplicationServiceType(tSpan.getServiceType());
            spanBo.setApplicationServiceTypeName(registry.findServiceType(tSpan.getServiceType()).getName());
        }

        spanBo.setParentApplicationId(tSpan.getParentApplicationName());
        spanBo.setParentApplicationServiceType(tSpan.getParentApplicationType());
        spanBo.setParentApplicationServiceTypeName(registry.findServiceType(tSpan.getParentApplicationType()).getName());

        // FIXME span.errCode contains error of span and spanEvent
        // because exceptionInfo is the error information of span itself, exceptionInfo can be null even if errCode is not 0
        final TIntStringValue exceptionInfo = tSpan.getExceptionInfo();
        if (exceptionInfo != null) {
            spanBo.setExceptionInfo(exceptionInfo.getIntValue(), exceptionInfo.getStringValue());
        }

        List<AnnotationBo> annotationBoList = buildAnnotationList(tSpan.getAnnotations());
        spanBo.setAnnotationBoList(annotationBoList);

        return spanBo;
    }


    private void bind(SpanEventBo spanEvent, TSpanEvent tSpanEvent) {

        spanEvent.setSequence(tSpanEvent.getSequence());

        spanEvent.setStartElapsed(tSpanEvent.getStartElapsed());
        spanEvent.setEndElapsed(tSpanEvent.getEndElapsed());

        spanEvent.setRpc(tSpanEvent.getRpc());
        spanEvent.setServiceType(tSpanEvent.getServiceType());
        spanEvent.setServiceTypeName(registry.findServiceType(tSpanEvent.getServiceType()).getName());

        spanEvent.setDestinationId(tSpanEvent.getDestinationId());

        spanEvent.setEndPoint(tSpanEvent.getEndPoint());
        spanEvent.setApiId(tSpanEvent.getApiId());

        if (tSpanEvent.isSetDepth()) {
            spanEvent.setDepth(tSpanEvent.getDepth());
        }

        if (tSpanEvent.isSetNextSpanId()) {
            spanEvent.setNextSpanId(tSpanEvent.getNextSpanId());
        }

        List<AnnotationBo> annotationList = buildAnnotationList(tSpanEvent.getAnnotations());
        spanEvent.setAnnotationBoList(annotationList);

        final TIntStringValue exceptionInfo = tSpanEvent.getExceptionInfo();
        if (exceptionInfo != null) {
            spanEvent.setExceptionInfo(exceptionInfo.getIntValue(), exceptionInfo.getStringValue());
        }

        if (tSpanEvent.isSetAsyncId()) {
            spanEvent.setAsyncId(tSpanEvent.getAsyncId());
        }

        if (tSpanEvent.isSetNextAsyncId()) {
            spanEvent.setNextAsyncId(tSpanEvent.getNextAsyncId());
        }

        if (tSpanEvent.isSetAsyncSequence()) {
            spanEvent.setAsyncSequence(tSpanEvent.getAsyncSequence());
        }
    }



    public SpanChunkBo buildSpanChunkBo(TSpanChunk tSpanChunk) {
        final SpanChunkBo spanChunkBo = newSpanChunkBo(tSpanChunk);

        List<TSpanEvent> spanEventList = tSpanChunk.getSpanEventList();
        List<SpanEventBo> spanEventBoList = buildSpanEventBoList(spanEventList);
        spanChunkBo.addSpanEventBoList(spanEventBoList);


        long acceptedTime = acceptedTimeService.getAcceptedTime();
        spanChunkBo.setCollectorAcceptTime(acceptedTime);

        return spanChunkBo;
    }

    // for test
    SpanChunkBo newSpanChunkBo(TSpanChunk tSpanChunk) {
        final SpanChunkBo spanChunkBo = new SpanChunkBo();
        spanChunkBo.setAgentId(tSpanChunk.getAgentId());
        spanChunkBo.setApplicationId(tSpanChunk.getApplicationName());
        spanChunkBo.setAgentStartTime(tSpanChunk.getAgentStartTime());
        spanChunkBo.setServiceType(tSpanChunk.getServiceType());
        spanChunkBo.setServiceTypeName(registry.findServiceType(tSpanChunk.getServiceType()).getName());
        if (tSpanChunk.isSetApplicationServiceType()) {
            spanChunkBo.setApplicationServiceType(tSpanChunk.getApplicationServiceType());
            spanChunkBo.setApplicationServiceTypeName(registry.findServiceType(tSpanChunk.getApplicationServiceType()).getName());
        } else {
            spanChunkBo.setApplicationServiceType(tSpanChunk.getServiceType());
            spanChunkBo.setApplicationServiceTypeName(registry.findServiceType(tSpanChunk.getServiceType()).getName());
        }

        TransactionId transactionId = newTransactionId(tSpanChunk.getTransactionId(), spanChunkBo);
        spanChunkBo.setTransactionId(transactionId);


        spanChunkBo.setSpanId(tSpanChunk.getSpanId());
        spanChunkBo.setEndPoint(tSpanChunk.getEndPoint());
        return spanChunkBo;
    }

    private TransactionId newTransactionId(byte[] transactionIdBytes, BasicSpan basicSpan) {
        final TransactionId transactionId = TransactionIdUtils.parseTransactionId(transactionIdBytes);
        String transactionAgentId = transactionId.getAgentId();
        if (transactionAgentId != null) {
            return transactionId;
        }
        String spanAgentId = basicSpan.getAgentId();
        return new TransactionId(spanAgentId, transactionId.getAgentStartTime(), transactionId.getTransactionSequence());
    }


    private List<SpanEventBo> buildSpanEventBoList(List<TSpanEvent> spanEventList) {
        if (CollectionUtils.isEmpty(spanEventList)) {
            return new ArrayList<SpanEventBo>();
        }
        List<SpanEventBo> spanEventBoList = new ArrayList<SpanEventBo>(spanEventList.size());
        for (TSpanEvent tSpanEvent : spanEventList) {
            final SpanEventBo spanEventBo = buildSpanEventBo(tSpanEvent);
            if (!spanEventFilter.filter(spanEventBo)) {
                continue;
            }
            spanEventBoList.add(spanEventBo);
        }

        Collections.sort(spanEventBoList, SpanEventComparator.INSTANCE);
        return spanEventBoList;
    }

    private List<AnnotationBo> buildAnnotationList(List<TAnnotation> tAnnotationList) {
        if (tAnnotationList == null) {
            return new ArrayList<AnnotationBo>();
        }
        List<AnnotationBo> boList = new ArrayList<AnnotationBo>(tAnnotationList.size());
        for (TAnnotation tAnnotation : tAnnotationList) {
            final AnnotationBo annotationBo = newAnnotationBo(tAnnotation);
            boList.add(annotationBo);
        }

        Collections.sort(boList, AnnotationComparator.INSTANCE);
        return boList;
    }

    // for test
    public SpanEventBo buildSpanEventBo(TSpanEvent tSpanEvent) {
        if (tSpanEvent == null) {
            throw new NullPointerException("tSpanEvent must not be null");
        }

        final SpanEventBo spanEvent = new SpanEventBo();
        bind(spanEvent, tSpanEvent);
        return spanEvent;
    }

    private AnnotationBo newAnnotationBo(TAnnotation tAnnotation) {
        if (tAnnotation == null) {
            throw new NullPointerException("annotation must not be null");
        }
        AnnotationBo annotationBo = new AnnotationBo();

        annotationBo.setKey(tAnnotation.getKey());

        Object value = transcoder.getMappingValue(tAnnotation);
        annotationBo.setValue(value);

        return annotationBo;
    }
}
