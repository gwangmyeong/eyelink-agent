package com.m2u.eyelink.collector.dao.elasticsearch;

import com.m2u.eyelink.collector.common.elasticsearch.AbstractRowKeyDistributor;
import com.m2u.eyelink.collector.common.elasticsearch.Scan;

public class RowKeyDistributorByHashPrefix implements AbstractRowKeyDistributor {
	private Hasher hasher;

	public RowKeyDistributorByHashPrefix(Hasher hasher) {
		this.hasher = hasher;
	}

	public byte[] getDistributedKey(byte[] rowKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Scan[] getDistributedScans(Scan originalScan) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getOriginalKey(Object row) {
		// TODO Auto-generated method stub
		return null;
	}
}
