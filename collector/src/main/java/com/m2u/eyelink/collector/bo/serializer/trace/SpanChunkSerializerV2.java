package com.m2u.eyelink.collector.bo.serializer.trace;

import java.nio.ByteBuffer;

import org.springframework.stereotype.Component;

import com.m2u.eyelink.collector.bo.serializer.ElasticSearchSerializer;
import com.m2u.eyelink.collector.bo.serializer.SerializationContext;
import com.m2u.eyelink.collector.common.elasticsearch.Put;
import com.m2u.eyelink.collector.dao.SpanChunkBo;

import static com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchTables.*;

@Component
public class SpanChunkSerializerV2 implements ElasticSearchSerializer<SpanChunkBo, Put> {

    private final SpanEncoder spanEncoder = new SpanEncoderV0();

    @Override
    public void serialize(SpanChunkBo spanChunkBo, Put put, SerializationContext context) {
        if (spanChunkBo == null) {
            throw new NullPointerException("spanChunkBo must not be null");
        }

        SpanEncodingContext<SpanChunkBo> encodingContext = new SpanEncodingContext<SpanChunkBo>(spanChunkBo);

        ByteBuffer qualifier = spanEncoder.encodeSpanChunkQualifier(encodingContext);
        ByteBuffer columnValue = spanEncoder.encodeSpanChunkColumnValue(encodingContext);

        long acceptedTime = put.getTimeStamp();
        put.addColumn(TRACE_V2_CF_SPAN, qualifier, acceptedTime, columnValue);

    }

}
