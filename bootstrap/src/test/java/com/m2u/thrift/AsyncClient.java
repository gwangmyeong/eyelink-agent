package com.m2u.thrift;

import java.io.IOException;

import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TNonblockingSocket;

public class AsyncClient {
	private void invoke() throws IOException, org.apache.thrift.TException {
		ArithmeticService.AsyncClient client = new ArithmeticService.AsyncClient(
				new TBinaryProtocol.Factory(), new TAsyncClientManager(),
				new TNonblockingSocket("localhost", 7911));

		client.add(200, 400, new AddMethodCallback());
		client = new ArithmeticService.AsyncClient(
				new TBinaryProtocol.Factory(), new TAsyncClientManager(),
				new TNonblockingSocket("localhost", 7911));
		client.multiply(10, 20,  new MultiplyMethodCallback());
	}
	
	public static void main(String [] args) throws IOException, org.apache.thrift.TException {
		AsyncClient c = new AsyncClient();
		c.invoke();
	}
	
	class AddMethodCallback implements AsyncMethodCallback<ArithmeticService.AsyncClient.add_call> {
		public void onComplete(ArithmeticService.AsyncClient.add_call add_call) {
			try {
				long result = add_call.getResult();
				System.out.println("Add from server : " + result);
			} catch (TException e) {
				e.printStackTrace();
			}
		}
		
		public void onError(Exception e) {
			System.out.println("Error : ");
			e.printStackTrace();
		}
	}
	
	class MultiplyMethodCallback implements AsyncMethodCallback<ArithmeticService.AsyncClient.multiply_call> {
		public void onComplete(ArithmeticService.AsyncClient.multiply_call multiply_call) {
			try {
				long result = multiply_call.getResult();
				System.out.println("Multiply from server: " + result);
			} catch (TException e) {
				e.printStackTrace();
			}
		}
		
		public void onError(Exception e) {
			System.out.println("Error : ");
			e.printStackTrace();
		}
	}

}
