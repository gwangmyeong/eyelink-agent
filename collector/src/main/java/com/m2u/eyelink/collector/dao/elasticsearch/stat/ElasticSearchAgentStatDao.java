package com.m2u.eyelink.collector.dao.elasticsearch.stat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.m2u.eyelink.collector.bo.ActiveTraceHistogramBo;
import com.m2u.eyelink.collector.bo.serializer.stat.AgentStatUtils;
import com.m2u.eyelink.collector.common.elasticsearch.AbstractRowKeyDistributor;
import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchOperations2;
import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchTables;
import com.m2u.eyelink.collector.common.elasticsearch.Put;
import com.m2u.eyelink.collector.dao.AgentStatDao;
import com.m2u.eyelink.collector.dao.elasticsearch.Bytes;
import com.m2u.eyelink.collector.mapper.thrift.ActiveTraceHistogramBoMapper;
import com.m2u.eyelink.collector.util.ElasticSearchUtils;
import com.m2u.eyelink.collector.util.RowKeyUtils;
import com.m2u.eyelink.collector.util.TimeUtils;
import com.m2u.eyelink.thrift.TActiveTrace;
import com.m2u.eyelink.thrift.TAgentStat;
import com.m2u.eyelink.thrift.TCpuLoad;
import com.m2u.eyelink.thrift.TJvmGc;
import com.m2u.eyelink.thrift.TTransaction;
import com.m2u.eyelink.thrift.dto.TJvmGcType;
import com.m2u.eyelink.util.BytesUtils;

import static com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchTables.*;

@Repository
@Deprecated
public class ElasticSearchAgentStatDao implements AgentStatDao {

	@Autowired
    private ElasticSearchOperations2 elasticSearchTemplate;

    @Autowired
    @Qualifier("agentStatRowKeyDistributor")
    private AbstractRowKeyDistributor rowKeyDistributor;

    @Autowired
    private ActiveTraceHistogramBoMapper activeTraceHistogramBoMapper;

    public void insert(final TAgentStat agentStat) {
        if (agentStat == null) {
            throw new NullPointerException("agentStat must not be null");
        }
        Put put = createPut(agentStat);

        boolean success = elasticSearchTemplate.asyncPut(ElasticSearchUtils.generateIndexName(agentStat.getAgentId()), ElasticSearchTables.TYPE_AGENT_STAT, agentStat.getMap());
        if (!success) {
            elasticSearchTemplate.put(ElasticSearchTables.AGENT_STAT, put);
        }
    }

    private Put createPut(TAgentStat agentStat) {
        long timestamp = agentStat.getTimestamp();
        byte[] key = getDistributedRowKey(agentStat, timestamp);

        Put put = new Put(key);

        final long collectInterval = agentStat.getCollectInterval();
        put.addColumn(AGENT_STAT_CF_STATISTICS, AGENT_STAT_COL_INTERVAL, Bytes.toBytes(collectInterval));
        // GC, Memory
        if (agentStat.isSetGc()) {
            TJvmGc gc = agentStat.getGc();
            put.addColumn(AGENT_STAT_CF_STATISTICS, AGENT_STAT_COL_GC_TYPE, Bytes.toBytes(gc.getType().name()));
            put.addColumn(AGENT_STAT_CF_STATISTICS, AGENT_STAT_COL_GC_OLD_COUNT, Bytes.toBytes(gc.getJvmGcOldCount()));
            put.addColumn(AGENT_STAT_CF_STATISTICS, AGENT_STAT_COL_GC_OLD_TIME, Bytes.toBytes(gc.getJvmGcOldTime()));
            put.addColumn(AGENT_STAT_CF_STATISTICS, AGENT_STAT_COL_HEAP_USED, Bytes.toBytes(gc.getJvmMemoryHeapUsed()));
            put.addColumn(AGENT_STAT_CF_STATISTICS, AGENT_STAT_COL_HEAP_MAX, Bytes.toBytes(gc.getJvmMemoryHeapMax()));
            put.addColumn(AGENT_STAT_CF_STATISTICS, AGENT_STAT_COL_NON_HEAP_USED, Bytes.toBytes(gc.getJvmMemoryNonHeapUsed()));
            put.addColumn(AGENT_STAT_CF_STATISTICS, AGENT_STAT_COL_NON_HEAP_MAX, Bytes.toBytes(gc.getJvmMemoryNonHeapMax()));
        } else {
            put.addColumn(AGENT_STAT_CF_STATISTICS, AGENT_STAT_COL_GC_TYPE, Bytes.toBytes(TJvmGcType.UNKNOWN.name()));
        }
        // CPU
        if (agentStat.isSetCpuLoad()) {
            TCpuLoad cpuLoad = agentStat.getCpuLoad();
            double jvmCpuLoad = AgentStatUtils.convertLongToDouble(AgentStatUtils.convertDoubleToLong(cpuLoad.getJvmCpuLoad()));
            double systemCpuLoad = AgentStatUtils.convertLongToDouble(AgentStatUtils.convertDoubleToLong(cpuLoad.getSystemCpuLoad()));
            put.addColumn(AGENT_STAT_CF_STATISTICS, AGENT_STAT_COL_JVM_CPU, Bytes.toBytes(jvmCpuLoad));
            put.addColumn(AGENT_STAT_CF_STATISTICS, AGENT_STAT_COL_SYS_CPU, Bytes.toBytes(systemCpuLoad));
        }
        // Transaction
        if (agentStat.isSetTransaction()) {
            TTransaction transaction = agentStat.getTransaction();
            put.addColumn(AGENT_STAT_CF_STATISTICS, AGENT_STAT_COL_TRANSACTION_SAMPLED_NEW, Bytes.toBytes(transaction.getSampledNewCount()));
            put.addColumn(AGENT_STAT_CF_STATISTICS, AGENT_STAT_COL_TRANSACTION_SAMPLED_CONTINUATION, Bytes.toBytes(transaction.getSampledContinuationCount()));
            put.addColumn(AGENT_STAT_CF_STATISTICS, AGENT_STAT_COL_TRANSACTION_UNSAMPLED_NEW, Bytes.toBytes(transaction.getUnsampledNewCount()));
            put.addColumn(AGENT_STAT_CF_STATISTICS, AGENT_STAT_COL_TRANSACTION_UNSAMPLED_CONTINUATION, Bytes.toBytes(transaction.getUnsampledContinuationCount()));
        }
        // Active Trace
        if (agentStat.isSetActiveTrace()) {
            TActiveTrace activeTrace = agentStat.getActiveTrace();
            if (activeTrace.isSetHistogram()) {
                ActiveTraceHistogramBo activeTraceHistogramBo = this.activeTraceHistogramBoMapper.map(activeTrace.getHistogram());
                put.addColumn(AGENT_STAT_CF_STATISTICS, AGENT_STAT_COL_ACTIVE_TRACE_HISTOGRAM, activeTraceHistogramBo.writeValue());
            }
        }
        return put;
    }

    /**
     * Create row key based on the timestamp
     */
    private byte[] getRowKey(String agentId, long timestamp) {
        if (agentId == null) {
            throw new IllegalArgumentException("agentId must not null");
        }
        byte[] bAgentId = BytesUtils.toBytes(agentId);
        return RowKeyUtils.concatFixedByteAndLong(bAgentId, AGENT_NAME_MAX_LEN, TimeUtils.reverseTimeMillis(timestamp));
    }

    /**
     * Create row key based on the timestamp and distribute it into different buckets
     */
    private byte[] getDistributedRowKey(TAgentStat agentStat, long timestamp) {
        byte[] key = getRowKey(agentStat.getAgentId(), timestamp);
        return rowKeyDistributor.getDistributedKey(key);
    }

}
