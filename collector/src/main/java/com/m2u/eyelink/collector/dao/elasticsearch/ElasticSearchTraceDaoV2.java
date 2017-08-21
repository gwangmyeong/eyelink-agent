package com.m2u.eyelink.collector.dao.elasticsearch;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.m2u.eyelink.collector.bo.SpanBo;
import com.m2u.eyelink.collector.bo.SpanEventBo;
import com.m2u.eyelink.collector.bo.serializer.RowKeyEncoder;
import com.m2u.eyelink.collector.bo.serializer.trace.SpanChunkSerializerV2;
import com.m2u.eyelink.collector.bo.serializer.trace.SpanSerializerV2;
import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchOperations2;
import com.m2u.eyelink.collector.common.elasticsearch.Put;
import com.m2u.eyelink.collector.common.elasticsearch.TableName;
import com.m2u.eyelink.collector.dao.SpanChunkBo;
import com.m2u.eyelink.collector.dao.TraceDao;
import com.m2u.eyelink.util.CollectionUtils;
import com.m2u.eyelink.util.TransactionId;

import static com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchTables.*;

@Repository
public class ElasticSearchTraceDaoV2 implements TraceDao {


	private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ElasticSearchOperations2 elasticSearchTemplate;


    @Autowired
    private SpanSerializerV2 spanSerializer;

    @Autowired
    private SpanChunkSerializerV2 spanChunkSerializer;

    @Autowired
    @Qualifier("traceRowKeyEncoderV2")
    private RowKeyEncoder<TransactionId> rowKeyEncoder;


    @Override
    public void insert(final SpanBo spanBo) {
        if (spanBo == null) {
            throw new NullPointerException("spanBo must not be null");
        }


        long acceptedTime = spanBo.getCollectorAcceptTime();

        TransactionId transactionId = spanBo.getTransactionId();
        final byte[] rowKey = this.rowKeyEncoder.encodeRowKey(transactionId);
        final Put put = new Put(rowKey, acceptedTime);

        this.spanSerializer.serialize(spanBo, put, null);


        boolean success = elasticSearchTemplate.asyncPut(TRACE_V2, put);
        if (!success) {
            elasticSearchTemplate.put(TRACE_V2, put);
        }

    }



    @Override
    public void insertSpanChunk(SpanChunkBo spanChunkBo) {

        TransactionId transactionId = spanChunkBo.getTransactionId();
        final byte[] rowKey = this.rowKeyEncoder.encodeRowKey(transactionId);

        final long acceptedTime = spanChunkBo.getCollectorAcceptTime();
        final Put put = new Put(rowKey, acceptedTime);

        final List<SpanEventBo> spanEventBoList = spanChunkBo.getSpanEventBoList();
        if (CollectionUtils.isEmpty(spanEventBoList)) {
            return;
        }

        this.spanChunkSerializer.serialize(spanChunkBo, put, null);

        if (!put.isEmpty()) {
            boolean success = elasticSearchTemplate.asyncPut(TRACE_V2, put);
            if (!success) {
                elasticSearchTemplate.put(TRACE_V2, put);
            }
        }
    }





}
