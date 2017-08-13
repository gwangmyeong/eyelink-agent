package com.m2u.eyelink.collector.dao.elasticsearch;

public class RowKeyDistributorByHashPrefix {
	private Hasher hasher;

	public RowKeyDistributorByHashPrefix(Hasher hasher) {
		this.hasher = hasher;
	}

	public byte[] getDistributedKey(byte[] rowKey) {
		// TODO Auto-generated method stub
		return null;
	}
}
