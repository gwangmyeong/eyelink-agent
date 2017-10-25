package com.m2u.eyelink.collector.common.elasticsearch;

public interface AbstractRowKeyDistributor {

	Scan[] getDistributedScans(Scan originalScan);

	byte[] getOriginalKey(Object row);

	byte[] getDistributedKey(byte[] key);

}
