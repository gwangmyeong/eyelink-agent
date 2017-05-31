package com.m2u.eyelink.agent.profiler.monitor.collector.transaction;

import com.m2u.eyelink.context.thrift.TTransaction;

public class UnsupportedTransactionMetricCollector implements TransactionMetricCollector {

    @Override
    public TTransaction collect() {
        return null;
    }

    @Override
    public String toString() {
        return "UnsupportedTransactionMetricCollector";
    }
}
