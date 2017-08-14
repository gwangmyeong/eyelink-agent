package com.m2u.eyelink.collector.thrift.io;

import java.util.Arrays;

import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocolFactory;

import com.m2u.eyelink.context.thrift.HeaderTBaseSerializer;
import com.m2u.eyelink.context.thrift.HeaderTBaseSerializerFactory;
import com.m2u.eyelink.context.thrift.SerializerFactory;
import com.m2u.eyelink.context.thrift.TBaseLocator;
import com.m2u.eyelink.context.thrift.TCommandRegistry;
import com.m2u.eyelink.context.thrift.TCommandType;
import com.m2u.eyelink.context.thrift.ThreadLocalHeaderTBaseSerializerFactory;

public final class CommandHeaderTBaseSerializerFactory implements SerializerFactory<HeaderTBaseSerializer> {

    public static final int DEFAULT_SERIALIZER_MAX_SIZE = 1024 * 64;

    private final SerializerFactory<HeaderTBaseSerializer> factory;

    public CommandHeaderTBaseSerializerFactory() {
        this(DEFAULT_SERIALIZER_MAX_SIZE);
    }

    public CommandHeaderTBaseSerializerFactory(int outputStreamSize) {
        TBaseLocator commandTbaseLocator = new TCommandRegistry(Arrays.asList(TCommandType.values()));

        TProtocolFactory protocolFactory = new TCompactProtocol.Factory();
        HeaderTBaseSerializerFactory serializerFactory = new HeaderTBaseSerializerFactory(true, outputStreamSize, protocolFactory, commandTbaseLocator);

        this.factory = new ThreadLocalHeaderTBaseSerializerFactory<HeaderTBaseSerializer>(serializerFactory);
    }

    @Override
    public HeaderTBaseSerializer createSerializer() {
        return this.factory.createSerializer();
    }

}
