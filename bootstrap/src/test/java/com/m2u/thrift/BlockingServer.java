package com.m2u.thrift;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;

public class BlockingServer {
	private void start() {
		try {
			TServerSocket serverTransport = new TServerSocket(7911);

			ArithmeticService.Processor processor = new ArithmeticService.Processor<ArithmeticService.Iface>(
					new ArithmeticServiceImpl());
			TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(
					serverTransport));
			System.out.println("Starting server on port 7911....");
			server.serve();
		} catch (TTransportException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		BlockingServer srv = new BlockingServer();
		srv.start();
	}
}
