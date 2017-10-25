package com.m2u.eyelink.agent.profiler.context.id;

public interface TransactionCounter {
	long getSampledNewCount();

	long getSampledContinuationCount();

	long getUnSampledNewCount();

	long getUnSampledContinuationCount();

	long getTotalTransactionCount();

}
