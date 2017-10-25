package com.m2u.eyelink.collector.common.elasticsearch;

import java.nio.ByteBuffer;

public class Put {

	public Put(byte[] rowKey) {
		// TODO Auto-generated constructor stub
	}

	public Put(byte[] rowKey, long acceptedTime) {
		// TODO Auto-generated constructor stub
	}

	public void addColumn(byte[] agentinfoCfInfo, byte[] agentinfoCfInfoIdentifier, byte[] agentInfoBoValue) {
		// TODO Auto-generated method stub
		
	}

	public void addColumn(byte[] familyName, byte[] qualifier, Long timestamp, byte[] value) {
		// TODO Auto-generated method stub
		
	}

	public void addColumn(byte[] agentStatCfStatistics, ByteBuffer qualifierBuffer, Long latestTimestamp,
			ByteBuffer valueBuffer) {
		// TODO Auto-generated method stub
		
	}

	public long getTimeStamp() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

}
