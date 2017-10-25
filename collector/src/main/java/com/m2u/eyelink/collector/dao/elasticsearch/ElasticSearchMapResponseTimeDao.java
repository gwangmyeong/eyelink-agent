package com.m2u.eyelink.collector.dao.elasticsearch;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchOperations2;
import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchTables;
import com.m2u.eyelink.collector.common.elasticsearch.Increment;
import com.m2u.eyelink.collector.dao.MapResponseTimeDao;
import com.m2u.eyelink.collector.dao.elasticsearch.statistics.CallRowKey;
import com.m2u.eyelink.collector.dao.elasticsearch.statistics.ColumnName;
import com.m2u.eyelink.collector.dao.elasticsearch.statistics.DefaultRowInfo;
import com.m2u.eyelink.collector.dao.elasticsearch.statistics.ResponseColumnName;
import com.m2u.eyelink.collector.dao.elasticsearch.statistics.RowInfo;
import com.m2u.eyelink.collector.dao.elasticsearch.statistics.RowKey;
import com.m2u.eyelink.collector.dao.elasticsearch.statistics.RowKeyMerge;
import com.m2u.eyelink.collector.server.util.AcceptedTimeService;
import com.m2u.eyelink.collector.util.ApplicationMapStatisticsUtils;
import com.m2u.eyelink.collector.util.ConcurrentCounterMap;
import com.m2u.eyelink.collector.util.TimeSlot;
import com.m2u.eyelink.common.trace.ServiceType;

@Repository
public class ElasticSearchMapResponseTimeDao implements MapResponseTimeDao {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ElasticSearchOperations2 elasticSearchTemplate;

    @Autowired
    private AcceptedTimeService acceptedTimeService;

    @Autowired
    private TimeSlot timeSlot;

    @Autowired
    @Qualifier("selfMerge")
    private RowKeyMerge rowKeyMerge;

    @Autowired
    @Qualifier("statisticsSelfRowKeyDistributor")
    private RowKeyDistributorByHashPrefix rowKeyDistributorByHashPrefix;

    private final boolean useBulk;

    private final ConcurrentCounterMap<RowInfo> counter = new ConcurrentCounterMap<>();

    public ElasticSearchMapResponseTimeDao() {
        this(true);
    }

    public ElasticSearchMapResponseTimeDao(boolean useBulk) {
        this.useBulk = useBulk;
    }

    @Override
    public void received(String applicationName, ServiceType applicationServiceType, String agentId, int elapsed, boolean isError) {
        if (applicationName == null) {
            throw new NullPointerException("applicationName must not be null");
        }
        if (agentId == null) {
            throw new NullPointerException("agentId must not be null");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("[Received] {} ({})[{}]", applicationName, applicationServiceType, agentId);
        }

        // make row key. rowkey is me
        final long acceptedTime = acceptedTimeService.getAcceptedTime();
        final long rowTimeSlot = timeSlot.getTimeSlot(acceptedTime);
        final RowKey selfRowKey = new CallRowKey(applicationName, applicationServiceType.getCode(), rowTimeSlot);

        final short slotNumber = ApplicationMapStatisticsUtils.getSlotNumber(applicationServiceType, elapsed, isError);
        final ColumnName selfColumnName = new ResponseColumnName(agentId, slotNumber);
        if (useBulk) {
            RowInfo rowInfo = new DefaultRowInfo(selfRowKey, selfColumnName);
            this.counter.increment(rowInfo, 1L);
        } else {
            final byte[] rowKey = getDistributedKey(selfRowKey.getRowKey());
            // column name is the name of caller app.
            byte[] columnName = selfColumnName.getColumnName();
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
        elasticSearchTemplate.incrementColumnValue(ElasticSearchTables.MAP_STATISTICS_SELF_VER2, rowKey, ElasticSearchTables.MAP_STATISTICS_SELF_VER2_CF_COUNTER, columnName, increment);
    }


    @Override
    public void flushAll() {
        if (!useBulk) {
            throw new IllegalStateException("useBulk is " + useBulk);
        }

        // update statistics by rowkey and column for now. need to update it by rowkey later.
        Map<RowInfo,ConcurrentCounterMap.LongAdder> remove = this.counter.remove();
        List<Increment> merge = rowKeyMerge.createBulkIncrement(remove, rowKeyDistributorByHashPrefix);
        if (!merge.isEmpty()) {
            if (logger.isDebugEnabled()) {
                logger.debug("flush {} Increment:{}", this.getClass().getSimpleName(), merge.size());
            }
            elasticSearchTemplate.increment(ElasticSearchTables.MAP_STATISTICS_SELF_VER2, merge);
        }
    }

    private byte[] getDistributedKey(byte[] rowKey) {
        return rowKeyDistributorByHashPrefix.getDistributedKey(rowKey);
    }
}
