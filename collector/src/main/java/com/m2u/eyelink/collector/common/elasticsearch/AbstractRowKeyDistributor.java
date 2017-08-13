package com.m2u.eyelink.collector.common.elasticsearch;

public interface AbstractRowKeyDistributor {

	Scan[] getDistributedScans(Scan originalScan);

	Object getOriginalKey(Object row);

}
