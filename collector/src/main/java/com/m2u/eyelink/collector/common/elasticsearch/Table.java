package com.m2u.eyelink.collector.common.elasticsearch;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.Op.Delete;

public class Table {

	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}

	public ResultScanner getScanner(Scan scan) {
		// TODO Auto-generated method stub
		return null;
	}

	public Result get(Get get) {
		// TODO Auto-generated method stub
		return null;
	}

	public Result[] get(List<Get> getList) {
		// TODO Auto-generated method stub
		return null;
	}

	public void put(Put put) {
		// TODO Auto-generated method stub
		
	}

	public void put(List<Put> puts) {
		// TODO Auto-generated method stub
		
	}

	public void delete(Delete delete) {
		// TODO Auto-generated method stub
		
	}

	public void delete(List<Delete> deletes) {
		// TODO Auto-generated method stub
		
	}

	public Result increment(Increment increment) {
		// TODO Auto-generated method stub
		return null;
	}

	public Long incrementColumnValue(byte[] rowName, byte[] familyName, byte[] qualifier, long amount) {
		// TODO Auto-generated method stub
		return null;
	}

	public Long incrementColumnValue(byte[] rowName, byte[] familyName, byte[] qualifier, long amount, Object object) {
		// TODO Auto-generated method stub
		return null;
	}

}
