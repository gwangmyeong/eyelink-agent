package com.m2u.eyelink.collector.common.elasticsearch;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

public class ConnectionFactory {

	public static Connection createConnection(Configuration configuration) {
		// FIXME add ES Connection logic 
		return new Connection();
	}

	public static Connection createConnection(Configuration config, ExecutorService executor) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
