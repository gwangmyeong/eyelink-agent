package com.m2u.eyelink.collector.mapper.thrift.stat;

import org.apache.thrift.TBase;

public interface ThriftBoMapper<T, F extends TBase<?,?>> {

    T map(F thriftObject);
}
