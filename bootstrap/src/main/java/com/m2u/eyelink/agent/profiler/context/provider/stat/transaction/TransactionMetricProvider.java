package com.m2u.eyelink.agent.profiler.context.provider.stat.transaction;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.m2u.eyelink.agent.profiler.context.id.TransactionCounter;
import com.m2u.eyelink.agent.profiler.monitor.metric.transaction.DefaultTransactionMetric;
import com.m2u.eyelink.agent.profiler.monitor.metric.transaction.TransactionMetric;

public class TransactionMetricProvider implements Provider<TransactionMetric> {

    private final TransactionCounter transactionCounter;

    @Inject
    public TransactionMetricProvider(TransactionCounter transactionCounter) {
        this.transactionCounter = transactionCounter;
    }

    @Override
    public TransactionMetric get() {
        if (transactionCounter == null) {
            return TransactionMetric.UNSUPPORTED_TRANSACTION_METRIC;
        }
        return new DefaultTransactionMetric(transactionCounter);
    }
}
