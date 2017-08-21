package com.m2u.eyelink.collector.bo.serializer.stat;

import java.math.BigDecimal;

import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchTables;

public class AgentStatUtils {

    public static final int NUM_DECIMALS = 4;
    public static final long CONVERT_VALUE = (long) Math.pow(10, NUM_DECIMALS);

    public static long convertDoubleToLong(double value) {
        long convertedValue = (long) (value * CONVERT_VALUE);
        return convertedValue;
    }

    public static double convertLongToDouble(long value) {
        double convertedValue = ((double) value) / CONVERT_VALUE;
        return convertedValue;
    }

    public static double calculateRate(long count, long timeMs, int numDecimals, double defaultRate) {
        if (numDecimals < 0) {
            throw new IllegalArgumentException("numDecimals must be greater than 0");
        }
        if (timeMs < 1) {
            return defaultRate;
        }
        return new BigDecimal(count / (timeMs / 1000D)).setScale(numDecimals, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static long getBaseTimestamp(long timestamp) {
        return timestamp - (timestamp % ElasticSearchTables.AGENT_STAT_TIMESPAN_MS);
    }
}
