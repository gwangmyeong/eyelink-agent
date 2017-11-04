package com.m2u.eyelink.thrift;

import java.util.Arrays;

import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocolFactory;

import com.m2u.eyelink.thrift.DeserializerFactory;
import com.m2u.eyelink.thrift.HeaderTBaseDeserializer;
import com.m2u.eyelink.thrift.HeaderTBaseDeserializerFactory;
import com.m2u.eyelink.thrift.TBaseLocator;
import com.m2u.eyelink.thrift.TCommandRegistry;
import com.m2u.eyelink.thrift.TCommandType;
import com.m2u.eyelink.thrift.ThreadLocalHeaderTBaseDeserializerFactory;

public final class CommandHeaderTBaseDeserializerFactory implements DeserializerFactory<HeaderTBaseDeserializer> {

    private final DeserializerFactory<HeaderTBaseDeserializer> factory;

    public CommandHeaderTBaseDeserializerFactory() {
        TBaseLocator commandTbaseLocator = commandTbaseLocator = new TCommandRegistry(Arrays.asList(TCommandType.values()));

        TProtocolFactory protocolFactory = new TCompactProtocol.Factory();
        HeaderTBaseDeserializerFactory deserializerFactory = new HeaderTBaseDeserializerFactory(protocolFactory, commandTbaseLocator);

        this.factory = new ThreadLocalHeaderTBaseDeserializerFactory<HeaderTBaseDeserializer>(deserializerFactory);
    }

    @Override
    public HeaderTBaseDeserializer createDeserializer() {
        return this.factory.createDeserializer();
    }

}
