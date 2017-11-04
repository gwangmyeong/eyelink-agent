package com.m2u.eyelink.collector.dao.elasticsearch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.m2u.eyelink.collector.bo.SpanBo;
import com.m2u.eyelink.collector.bo.SpanEventBo;
import com.m2u.eyelink.collector.bo.SpanFactory;
import com.m2u.eyelink.collector.common.elasticsearch.AbstractRowKeyDistributor;
import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchOperations2;
import com.m2u.eyelink.collector.common.elasticsearch.Put;
import com.m2u.eyelink.collector.common.elasticsearch.TableName;
import com.m2u.eyelink.collector.dao.ApplicationTraceIndexDao;
import com.m2u.eyelink.collector.server.util.AcceptedTimeService;
import com.m2u.eyelink.collector.util.ElasticSearchUtils;
import com.m2u.eyelink.collector.util.SpanUtils;
import com.m2u.eyelink.context.TIntStringValue;
import com.m2u.eyelink.thrift.TSpan;
import com.m2u.eyelink.thrift.dto.TSpanEvent;
import com.m2u.eyelink.util.AutomaticBuffer;
import com.m2u.eyelink.util.Buffer;

import static com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchTables.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ElasticSearchApplicationTraceIndexDao implements ApplicationTraceIndexDao {

	@Autowired
    private ElasticSearchOperations2 elasticSearchTemplate;

    @Autowired
    private AcceptedTimeService acceptedTimeService;

    @Autowired
    @Qualifier("applicationTraceIndexDistributor")
    private AbstractRowKeyDistributor rowKeyDistributor;

    @Autowired
    private SpanFactory spanFactory;

    @Override
    public void insert(final TSpan span) {
        if (span == null) {
            throw new NullPointerException("span must not be null");
        }

        
        final Buffer buffer = new AutomaticBuffer(10 + AGENT_NAME_MAX_LEN);
        buffer.putVInt(span.getElapsed());
        buffer.putSVInt(span.getErr());
        buffer.putPrefixedString(span.getAgentId());
        final byte[] value = buffer.getBuffer();

        long acceptedTime = acceptedTimeService.getAcceptedTime();
        final byte[] distributedKey = createRowKey(span, acceptedTime);
        Put put = new Put(distributedKey);

        put.addColumn(APPLICATION_TRACE_INDEX_CF_TRACE, makeQualifier(span) , acceptedTime, value);

        final SpanBo spanBo = spanFactory.buildSpanBo(span);

        // TODO check logic, insert same data this and ElasticSearchTraceDaoV2
        boolean success = elasticSearchTemplate.asyncPut(ElasticSearchUtils.generateIndexName(span.getAgentId()), TYPE_APPLICATION_TRACE_INDEX, spanBo.getMap());
        
        if (!success) {
            elasticSearchTemplate.put(ElasticSearchUtils.generateIndexName(span.getAgentId()), TYPE_APPLICATION_TRACE_INDEX, spanBo.getMap());
        }
    }

    private byte[] makeQualifier(final TSpan span) {
        byte[] qualifier = SpanUtils.getVarTransactionId(span);

        return qualifier;
    }

    private byte[] createRowKey(TSpan span, long acceptedTime) {
        // distribute key evenly
        byte[] applicationTraceIndexRowKey = SpanUtils.getApplicationTraceIndexRowKey(span.getApplicationName(), acceptedTime);
        return rowKeyDistributor.getDistributedKey(applicationTraceIndexRowKey);
    }
}
