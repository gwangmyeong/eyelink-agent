package com.m2u.thrift;

import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TTransportException;

public class NonBlockingServer {

	private void start() {
		try {
			TNonblockingServerTransport serverTransport = new TNonblockingServerSocket(7911);
			
			ArithmeticService.Processor processor = new ArithmeticService.Processor<ArithmeticService.Iface>(new ArithmeticServiceImpl());
			
			TServer server = new TNonblockingServer(new TNonblockingServer.Args(serverTransport));
			System.out.println("Starting server on port 7911...");
			server.serve();
		} catch (TTransportException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		NonBlockingServer nbs = new NonBlockingServer();
		nbs.start();
	}
}
