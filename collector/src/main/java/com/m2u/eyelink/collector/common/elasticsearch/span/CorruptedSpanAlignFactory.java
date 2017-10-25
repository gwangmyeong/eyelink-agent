package com.m2u.eyelink.collector.common.elasticsearch.span;

import java.util.ArrayList;
import java.util.List;

import com.m2u.eyelink.collector.bo.AnnotationBo;
import com.m2u.eyelink.collector.bo.ApiMetaDataBo;
import com.m2u.eyelink.collector.bo.MethodTypeEnum;
import com.m2u.eyelink.collector.bo.SpanBo;
import com.m2u.eyelink.collector.bo.SpanEventBo;
import com.m2u.eyelink.common.trace.AnnotationKey;
import com.m2u.eyelink.common.trace.ServiceType;
import com.m2u.eyelink.util.AnnotationKeyUtils;

public class CorruptedSpanAlignFactory {
    // private static final long DEFAULT_TIMEOUT_MILLISEC = 10 * 60 * 1000;
    private static final long DEFAULT_TIMEOUT_MILLISEC = 60 * 1000;

    private long timeoutMillisec = DEFAULT_TIMEOUT_MILLISEC;

    public SpanAlign get(final String title, final SpanBo span, final SpanEventBo spanEvent) {
        final SpanEventBo missedEvent = new SpanEventBo();
        // TODO use invalid event information ?
        missedEvent.setStartElapsed(spanEvent.getStartElapsed());
        missedEvent.setEndElapsed(spanEvent.getEndElapsed());

        missedEvent.setServiceType(ServiceType.COLLECTOR.getCode());

        List<AnnotationBo> annotations = new ArrayList<>();

        ApiMetaDataBo apiMetaData = new ApiMetaDataBo();
        apiMetaData.setLineNumber(-1);
        apiMetaData.setApiInfo("...");
        apiMetaData.setMethodTypeEnum(MethodTypeEnum.CORRUPTED);

        final AnnotationBo apiMetaDataAnnotation = new AnnotationBo();
        apiMetaDataAnnotation.setKey(AnnotationKey.API_METADATA.getCode());
        apiMetaDataAnnotation.setValue(apiMetaData);
        annotations.add(apiMetaDataAnnotation);

        final AnnotationBo argumentAnnotation = new AnnotationBo();
        argumentAnnotation.setKey(AnnotationKeyUtils.getArgs(0).getCode());
        if (System.currentTimeMillis() - span.getStartTime() < timeoutMillisec) {
            argumentAnnotation.setValue("Corrupted(waiting for packet) ");
        } else {
            if (title != null) {
                argumentAnnotation.setValue("Corrupted(" + title + ")");
            } else {
                argumentAnnotation.setValue("Corrupted");
            }
        }
        annotations.add(argumentAnnotation);

        missedEvent.setAnnotationBoList(annotations);

        return new SpanAlign(span, missedEvent);
    }
}