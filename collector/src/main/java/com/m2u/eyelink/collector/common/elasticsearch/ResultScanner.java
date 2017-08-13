package com.m2u.eyelink.collector.common.elasticsearch;

public abstract interface ResultScanner extends java.io.Closeable, java.lang.Iterable {

	public abstract Result next() throws java.io.IOException;

	public abstract Result[] next(int arg0) throws java.io.IOException;

	void close();

}
