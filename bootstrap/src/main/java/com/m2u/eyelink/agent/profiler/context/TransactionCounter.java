package com.m2u.eyelink.agent.profiler.context;

public interface TransactionCounter {
	long getSampledNewCount();

	long getSampledContinuationCount();

	long getUnSampledNewCount();

	long getUnSampledContinuationCount();

	long getTotalTransactionCount();

}
