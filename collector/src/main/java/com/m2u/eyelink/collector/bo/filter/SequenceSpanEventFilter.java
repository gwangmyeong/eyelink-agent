package com.m2u.eyelink.collector.bo.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.collector.bo.SpanEventBo;

public class SequenceSpanEventFilter implements SpanEventFilter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final int MAX_SEQUENCE = Short.MAX_VALUE;
    public static final int DEFAULT_SEQUENCE_LIMIT = 1024*10;

    private final int sequenceLimit;

    public SequenceSpanEventFilter() {
        this(DEFAULT_SEQUENCE_LIMIT);
    }

    public SequenceSpanEventFilter(int sequenceLimit) {
        if (sequenceLimit > MAX_SEQUENCE) {
            throw new IllegalArgumentException(sequenceLimit + " > MAX_SEQUENCE");
        }
        this.sequenceLimit = sequenceLimit;
    }

    @Override
    public boolean filter(SpanEventBo spanEventBo) {
        if (spanEventBo == null) {
            return REJECT;
        }
        final int sequence = spanEventBo.getSequence();
        if (sequence > sequenceLimit) {
            if (logger.isDebugEnabled()) {
                logger.debug("discard spanEvent:{}", spanEventBo);
            }
            return REJECT;
        }
        return ACCEPT;
    }
}
