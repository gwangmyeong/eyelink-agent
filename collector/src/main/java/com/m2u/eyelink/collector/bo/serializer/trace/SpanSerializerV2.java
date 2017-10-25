package com.m2u.eyelink.collector.bo.serializer.trace;

import java.nio.ByteBuffer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.m2u.eyelink.collector.bo.SpanBo;
import com.m2u.eyelink.collector.bo.serializer.ElasticSearchSerializer;
import com.m2u.eyelink.collector.bo.serializer.SerializationContext;
import com.m2u.eyelink.collector.common.elasticsearch.Put;

import static com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchTables.*;

@Component
public class SpanSerializerV2 implements ElasticSearchSerializer<SpanBo, Put> {



	@Autowired
    private SpanEncoder spanEncoder;

    public SpanSerializerV2() {
    }


    @Override
    public void serialize(SpanBo spanBo, Put put, SerializationContext context) {

        final SpanEncodingContext<SpanBo> encodingContext = new SpanEncodingContext<SpanBo>(spanBo);

        ByteBuffer qualifier = spanEncoder.encodeSpanQualifier(encodingContext);
        ByteBuffer columnValue = spanEncoder.encodeSpanColumnValue(encodingContext);

        long acceptedTime = put.getTimeStamp();
        put.addColumn(TRACE_V2_CF_SPAN, qualifier, acceptedTime, columnValue);
    }



}
