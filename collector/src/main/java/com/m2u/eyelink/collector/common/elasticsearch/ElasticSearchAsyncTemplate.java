package com.m2u.eyelink.collector.common.elasticsearch;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.elasticsearch.client.transport.TransportClient;
import org.springframework.stereotype.Component;

// FIXME @Component not existed in pinpoint
//@Component
public class ElasticSearchAsyncTemplate implements ElasticSearchAsyncOperation {

    private final HTableMultiplexer hTableMultiplexer;
    private final AtomicInteger opsCount = new AtomicInteger();
    private final AtomicInteger opsRejectCount = new AtomicInteger();

    public ElasticSearchAsyncTemplate(Configuration conf, int perRegionServerBufferQueueSize) throws IOException {
        this.hTableMultiplexer = new HTableMultiplexer(conf, perRegionServerBufferQueueSize);
    }

    public ElasticSearchAsyncTemplate(TransportClient connection, Configuration conf, int perRegionServerBufferQueueSize) throws IOException {
        this.hTableMultiplexer = new HTableMultiplexer(connection, conf, perRegionServerBufferQueueSize);
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public boolean put(TableName tableName, Put put) {
        opsCount.incrementAndGet();

        boolean success = hTableMultiplexer.put(tableName, put);
        if (!success) {
            opsRejectCount.incrementAndGet();
        }
        return success;
    }

    @Override
    public List<Put> put(TableName tableName, List<Put> puts) {
        opsCount.addAndGet(puts.size());

        List<Put> rejectPuts = hTableMultiplexer.put(tableName, puts);
        if (rejectPuts != null && rejectPuts.size() > 0) {
            opsRejectCount.addAndGet(rejectPuts.size());
        }
        return rejectPuts;
    }

    @Override
    public Long getOpsCount() {
        return opsCount.longValue();
    }

    @Override
    public Long getOpsRejectedCount() {
        return opsRejectCount.longValue();
    }

    @Override
    public Long getCurrentOpsCount() {
        return hTableMultiplexer.getHTableMultiplexerStatus().getTotalBufferedCounter();
    }

    @Override
    public Long getOpsFailedCount() {
        return hTableMultiplexer.getHTableMultiplexerStatus().getTotalFailedCounter();
    }

    @Override
    public Long getOpsAverageLatency() {
        return hTableMultiplexer.getHTableMultiplexerStatus().getOverallAverageLatency();
    }

    @Override
    public Map<String, Long> getCurrentOpsCountForEachRegionServer() {
        return hTableMultiplexer.getHTableMultiplexerStatus().getBufferedCounterForEachRegionServer();
    }

    @Override
    public Map<String, Long> getOpsFailedCountForEachRegionServer() {
        return hTableMultiplexer.getHTableMultiplexerStatus().getFailedCounterForEachRegionServer();
    }

    @Override
    public Map<String, Long> getOpsAverageLatencyForEachRegionServer() {
        return hTableMultiplexer.getHTableMultiplexerStatus().getAverageLatencyForEachRegionServer();
    }

	@Override
	public boolean put(String indexName, String typeName, Map<String, Object> mapData) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean put(String indexName, String typeName, List<Map<String, Object>> listData) {
		// TODO Auto-generated method stub
		return false;
	}

}