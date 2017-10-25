package com.m2u.thrift;

import org.apache.thrift.TException;

public class ArithmeticServiceImpl implements ArithmeticService.Iface {

	@Override
	public long add(int num1, int num2) throws TException {
		return num1 + num2;
	}

	@Override
	public long multiply(int num1, int num2) throws TException {
		return num1 * num2;
	}

}
