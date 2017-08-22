package com.m2u.eyelink.collector.dao.elasticsearch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.m2u.eyelink.collector.bo.SpanEventBo;
import com.m2u.eyelink.collector.common.elasticsearch.AbstractRowKeyDistributor;
import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchOperations2;
import com.m2u.eyelink.collector.common.elasticsearch.Put;
import com.m2u.eyelink.collector.common.elasticsearch.TableName;
import com.m2u.eyelink.collector.dao.ApplicationTraceIndexDao;
import com.m2u.eyelink.collector.server.util.AcceptedTimeService;
import com.m2u.eyelink.collector.util.ElasticSearchUtils;
import com.m2u.eyelink.collector.util.SpanUtils;
import com.m2u.eyelink.context.TIntStringValue;
import com.m2u.eyelink.context.TSpanEvent;
import com.m2u.eyelink.context.thrift.TAnnotation;
import com.m2u.eyelink.context.thrift.TSpan;
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

        // FIXME, bsh, change logic to use Mapper Class
    		Map<String, Object> map = new HashMap<String, Object>();
    		map.put("agentId", span.getAgentId());
    		map.put("applicationName", span.getApplicationName());
    		map.put("agentStartTime", span.getAgentStartTime());
    		
//    		String transId = new String(span.getTransactionId(),0, span.getTransactionId().length);

    		map.put("transactionId", new String(span.getTransactionId()));
    		map.put("spanId", span.getSpanId());
    		map.put("parentSpanId", span.getParentSpanId());
    		map.put("startTime", span.getStartTime());
    		map.put("elapsed", span.getElapsed());
    		map.put("rpc", span.getRpc());
    		map.put("serviceType", span.getServiceType());
    		map.put("endPoint", span.getEndPoint());
    		map.put("remoteAddr", span.getRemoteAddr());
    		map.put("annotations", span.getAnnotations());
    		map.put("flag", span.getFlag());
    		map.put("err", span.getErr());
//    		map.put("spanEventList", span.getSpanEventList());
    		
    		List<Map<String,Object>> listEventBo = new ArrayList<Map<String,Object>>();
    		for(int i = 0; i < span.getSpanEventList().size(); i++) {
    			TSpanEvent eventBo = span.getSpanEventList().get(i);
    			Map<String,Object> map2 = new HashMap<String, Object>();
    			map2.put("spanId", eventBo.getSpanId());
    			map2.put("sequence", eventBo.getSequence());
    			map2.put("startElapsed", eventBo.getStartElapsed());
    			map2.put("endElapsed", eventBo.getEndElapsed());
    			map2.put("rpc", eventBo.getRpc());
    			map2.put("serviceType", eventBo.getServiceType());
    			map2.put("endPoint", eventBo.getEndPoint());
    			map2.put("annotations", eventBo.getAnnotations());    			
    			map2.put("depth", eventBo.getDepth());
    			map2.put("nextSpanId", eventBo.getNextSpanId());
    			map2.put("destinationId", eventBo.getDestinationId());
    			map2.put("apiId", eventBo.getApiId());
    			
    			map2.put("exceptionInfo", eventBo.getExceptionInfo());
    			map2.put("asyncId", eventBo.getAsyncId());
    			map2.put("nextAsyncId", eventBo.getNextAsyncId());
    			map2.put("asyncSequence", eventBo.getAsyncSequence());
    			  
    			listEventBo.add(map2);
    		}    	
    		map.put("spanEventList", listEventBo);
    		map.put("parentApplicationName", span.getParentApplicationName());
    		map.put("parentApplicationType", span.getParentApplicationType());
    		map.put("acceptorHost", span.getAcceptorHost());
    		map.put("apiId", span.getApiId());
    		map.put("exceptionInfo", span.getExceptionInfo());
    		map.put("applicationServiceType", span.getApplicationServiceType());
    		map.put("loggingTransactionInfo", span.getLoggingTransactionInfo());
    		
//        boolean success = elasticSearchTemplate.asyncPut(APPLICATION_TRACE_INDEX, put);
        boolean success = elasticSearchTemplate.asyncPut(ElasticSearchUtils.generateIndexName(span.getAgentId()), TYPE_APPLICATION_TRACE_INDEX, map);
        
        if (!success) {
            elasticSearchTemplate.put(ElasticSearchUtils.generateIndexName(span.getAgentId()), TYPE_APPLICATION_TRACE_INDEX, map);
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
