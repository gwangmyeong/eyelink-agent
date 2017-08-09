package com.m2u.eyelink.collector.common.elasticsearch;

import java.util.List;

public class HTableMultiplexer {

	public HTableMultiplexer(Configuration conf, int perRegionServerBufferQueueSize) {
		// TODO Auto-generated constructor stub
	}

	public HTableMultiplexer(Connection connection, Configuration conf, int perRegionServerBufferQueueSize) {
		// TODO Auto-generated constructor stub
	}

	public boolean put(TableName tableName, Put put) {
		// TODO Auto-generated method stub
		return false;
	}

	public List<Put> put(TableName tableName, List<Put> puts) {
		// TODO Auto-generated method stub
		return null;
	}

	public HTableMultiplexerStatus getHTableMultiplexerStatus() {
		// TODO Auto-generated method stub
		return new HTableMultiplexerStatus();
	}

}
