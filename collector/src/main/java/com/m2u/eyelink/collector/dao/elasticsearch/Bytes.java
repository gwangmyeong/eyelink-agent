package com.m2u.eyelink.collector.dao.elasticsearch;

import java.nio.ByteBuffer;

public class Bytes {

	public static byte[] toBytes(String string) {
		return string.getBytes();
	}

	public static int compareTo(Object originalKey, Object originalKey2) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static byte[] toBytes(short serviceType) {
		return new byte[] { (byte) (serviceType & 0x00FF), (byte) ((serviceType & 0xFF00) >> 8) };
	}

	public static byte[] toBytes(int code) {
		byte[] byteArray = new byte[4];
		byteArray[0] = (byte) (code >> 24);
		byteArray[1] = (byte) (code >> 16);
		byteArray[2] = (byte) (code >> 8);
		byteArray[3] = (byte) (code);
		return byteArray;
	}

	public static byte[] toBytes(long collectInterval) {
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
		buffer.putLong(collectInterval);
		return buffer.array();
	}

	public static byte[] toBytes(double jvmCpuLoad) {
		byte[] bytes = new byte[8];
		ByteBuffer.wrap(bytes).putDouble(jvmCpuLoad);
		return bytes;
	}

}
