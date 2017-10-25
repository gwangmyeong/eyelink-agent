package com.m2u.thrift;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

public class NonBlockingClient {
	private void invoke() {
		TTransport transport;
		try {
			transport = new TFramedTransport(new TSocket("localhost", 7911));
			TProtocol protocol = new TBinaryProtocol(transport);
			
			ArithmeticService.Client client = new ArithmeticService.Client(protocol);
			transport.open();
			
			long addResult = client.add(100, 200);
			System.out.println("Add Result : " + addResult);
			
			long multiplyResult = client.multiply(10,  20);
			System.out.println("Multiply Result : " + multiplyResult);
			
			transport.close();
		} catch (TTransportException e) {
			e.printStackTrace();
		} catch (TException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String [] args) {
		NonBlockingClient nbc = new NonBlockingClient();
		nbc.invoke();
	}
}
