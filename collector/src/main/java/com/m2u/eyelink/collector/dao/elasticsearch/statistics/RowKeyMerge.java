package com.m2u.eyelink.collector.dao.elasticsearch.statistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.collector.common.elasticsearch.Increment;
import com.m2u.eyelink.collector.dao.elasticsearch.RowKeyDistributorByHashPrefix;
import com.m2u.eyelink.collector.util.ConcurrentCounterMap;

public class RowKeyMerge {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final byte[] family;

    public RowKeyMerge(byte[] family) {
        if (family == null) {
            throw new NullPointerException("family must not be null");
        }
        this.family = Arrays.copyOf(family, family.length);
    }

    public  List<Increment> createBulkIncrement(Map<RowInfo, ConcurrentCounterMap.LongAdder> data, RowKeyDistributorByHashPrefix rowKeyDistributorByHashPrefix) {
        if (data.isEmpty()) {
            return Collections.emptyList();
        }

        final Map<RowKey, List<ColumnName>> rowkeyMerge = rowKeyBaseMerge(data);

        List<Increment> incrementList = new ArrayList<>();
        for (Map.Entry<RowKey, List<ColumnName>> rowKeyEntry : rowkeyMerge.entrySet()) {
            Increment increment = createIncrement(rowKeyEntry, rowKeyDistributorByHashPrefix);
            incrementList.add(increment);
        }
        return incrementList;
    }

    private Increment createIncrement(Map.Entry<RowKey, List<ColumnName>> rowKeyEntry, RowKeyDistributorByHashPrefix rowKeyDistributorByHashPrefix) {
        RowKey rowKey = rowKeyEntry.getKey();
        byte[] key = null;
        if (rowKeyDistributorByHashPrefix == null) {
            key = rowKey.getRowKey();
        } else {
            key = rowKeyDistributorByHashPrefix.getDistributedKey(rowKey.getRowKey());
        }
        final Increment increment = new Increment(key);
        for (ColumnName columnName : rowKeyEntry.getValue()) {
            increment.addColumn(family, columnName.getColumnName(), columnName.getCallCount());
        }
        logger.trace("create increment row:{}, column:{}", rowKey, rowKeyEntry.getValue());
        return increment;
    }

    private Map<RowKey, List<ColumnName>> rowKeyBaseMerge(Map<RowInfo, ConcurrentCounterMap.LongAdder> data) {
        final Map<RowKey, List<ColumnName>> merge = new HashMap<>();

        for (Map.Entry<RowInfo, ConcurrentCounterMap.LongAdder> entry : data.entrySet()) {
            final RowInfo rowInfo = entry.getKey();
            // write callCount to columnName and throw away
            long callCount = entry.getValue().get();
            rowInfo.getColumnName().setCallCount(callCount);

            RowKey rowKey = rowInfo.getRowKey();
            List<ColumnName> oldList = merge.get(rowKey);
            if (oldList == null) {
                List<ColumnName> newList = new ArrayList<>();
                newList.add(rowInfo.getColumnName());
                merge.put(rowKey, newList);
            } else {
                oldList.add(rowInfo.getColumnName());
            }
        }
        return merge;
    }
}
