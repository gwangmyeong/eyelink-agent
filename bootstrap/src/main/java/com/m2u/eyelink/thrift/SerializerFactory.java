package com.m2u.eyelink.thrift;

public interface SerializerFactory<E> {
	E createSerializer();
}
