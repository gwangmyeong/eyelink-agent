package com.m2u.eyelink.thrift;

public interface DeserializerFactory<T> {
	T createDeserializer();
}
