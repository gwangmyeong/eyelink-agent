package com.m2u.eyelink.collector.bo.serializer.trace;

import java.nio.ByteBuffer;

import com.m2u.eyelink.collector.bo.SpanBo;
import com.m2u.eyelink.collector.dao.SpanChunkBo;

public interface SpanEncoder {

    byte TYPE_SPAN = 0;
    byte TYPE_SPAN_CHUNK = 1;

    // reserved
    byte TYPE_PASSIVE_SPAN = 4;
    byte TYPE_INDEX = 7;

    ByteBuffer encodeSpanQualifier(SpanEncodingContext<SpanBo> encodingContext);

    ByteBuffer encodeSpanColumnValue(SpanEncodingContext<SpanBo> encodingContext);


    ByteBuffer encodeSpanChunkQualifier(SpanEncodingContext<SpanChunkBo> encodingContext);

    ByteBuffer encodeSpanChunkColumnValue(SpanEncodingContext<SpanChunkBo> encodingContext);
}
