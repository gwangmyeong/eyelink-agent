package com.m2u.eyelink.collector.dao.elasticsearch;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchOperations2;
import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchTables;
import com.m2u.eyelink.collector.common.elasticsearch.Increment;
import com.m2u.eyelink.collector.dao.MapStatisticsCallerDao;
import com.m2u.eyelink.collector.dao.elasticsearch.statistics.CallRowKey;
import com.m2u.eyelink.collector.dao.elasticsearch.statistics.CalleeColumnName;
import com.m2u.eyelink.collector.dao.elasticsearch.statistics.ColumnName;
import com.m2u.eyelink.collector.dao.elasticsearch.statistics.DefaultRowInfo;
import com.m2u.eyelink.collector.dao.elasticsearch.statistics.RowInfo;
import com.m2u.eyelink.collector.dao.elasticsearch.statistics.RowKey;
import com.m2u.eyelink.collector.dao.elasticsearch.statistics.RowKeyMerge;
import com.m2u.eyelink.collector.server.util.AcceptedTimeService;
import com.m2u.eyelink.collector.util.ApplicationMapStatisticsUtils;
import com.m2u.eyelink.collector.util.ConcurrentCounterMap;
import com.m2u.eyelink.collector.util.TimeSlot;
import com.m2u.eyelink.trace.ServiceType;

@Repository
public class ElasticSearchMapStatisticsCallerDao implements MapStatisticsCallerDao {


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ElasticSearchOperations2 elasticSearchTemplate;

    @Autowired
    private AcceptedTimeService acceptedTimeService;

    @Autowired
    @Qualifier("callerMerge")
    private RowKeyMerge rowKeyMerge;

    @Autowired
    @Qualifier("statisticsCallerRowKeyDistributor")
    private RowKeyDistributorByHashPrefix rowKeyDistributorByHashPrefix;

    @Autowired
    private TimeSlot timeSlot;

    private final boolean useBulk;

    private final ConcurrentCounterMap<RowInfo> counter = new ConcurrentCounterMap<>();

    public ElasticSearchMapStatisticsCallerDao() {
        this(true);
    }

    public ElasticSearchMapStatisticsCallerDao(boolean useBulk) {
        this.useBulk = useBulk;
    }

    @Override
    public void update(String callerApplicationName, ServiceType callerServiceType, String callerAgentid, String calleeApplicationName, ServiceType calleeServiceType, String calleeHost, int elapsed, boolean isError) {
        if (callerApplicationName == null) {
            throw new NullPointerException("callerApplicationName must not be null");
        }
        if (calleeApplicationName == null) {
            throw new NullPointerException("calleeApplicationName must not be null");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("[Caller] {} ({}) {} -> {} ({})[{}]", callerApplicationName, callerServiceType, callerAgentid,
                    calleeApplicationName, calleeServiceType, calleeHost);
        }

        // there may be no endpoint in case of httpclient
        calleeHost = StringUtils.defaultString(calleeHost);

        // make row key. rowkey is me
        final long acceptedTime = acceptedTimeService.getAcceptedTime();
        final long rowTimeSlot = timeSlot.getTimeSlot(acceptedTime);
        final RowKey callerRowKey = new CallRowKey(callerApplicationName, callerServiceType.getCode(), rowTimeSlot);

        final short calleeSlotNumber = ApplicationMapStatisticsUtils.getSlotNumber(calleeServiceType, elapsed, isError);
        final ColumnName calleeColumnName = new CalleeColumnName(callerAgentid, calleeServiceType.getCode(), calleeApplicationName, calleeHost, calleeSlotNumber);
        if (useBulk) {
            RowInfo rowInfo = new DefaultRowInfo(callerRowKey, calleeColumnName);
            this.counter.increment(rowInfo, 1L);
        } else {
            final byte[] rowKey = getDistributedKey(callerRowKey.getRowKey());
            // column name is the name of caller app.
            byte[] columnName = calleeColumnName.getColumnName();
            increment(rowKey, columnName, 1L);
        }
    }

    private void increment(byte[] rowKey, byte[] columnName, long increment) {
        if (rowKey == null) {
            throw new NullPointerException("rowKey must not be null");
        }
        if (columnName == null) {
            throw new NullPointerException("columnName must not be null");
        }
        elasticSearchTemplate.incrementColumnValue(ElasticSearchTables.MAP_STATISTICS_CALLEE_VER2, rowKey, ElasticSearchTables.MAP_STATISTICS_CALLEE_VER2_CF_COUNTER, columnName, increment);
    }

    @Override
    public void flushAll() {
        if (!useBulk) {
            throw new IllegalStateException();
        }
        // update statistics by rowkey and column for now. need to update it by rowkey later.
        Map<RowInfo,ConcurrentCounterMap.LongAdder> remove = this.counter.remove();
        List<Increment> merge = rowKeyMerge.createBulkIncrement(remove, rowKeyDistributorByHashPrefix);
        if (!merge.isEmpty()) {
            if (logger.isDebugEnabled()) {
                logger.debug("flush {} Increment:{}", this.getClass().getSimpleName(), merge.size());
            }
            elasticSearchTemplate.increment(ElasticSearchTables.MAP_STATISTICS_CALLEE_VER2, merge);
        }
    }

    private byte[] getDistributedKey(byte[] rowKey) {
        return rowKeyDistributorByHashPrefix.getDistributedKey(rowKey);
    }
}