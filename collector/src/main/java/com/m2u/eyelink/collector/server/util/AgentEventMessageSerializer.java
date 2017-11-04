package com.m2u.eyelink.collector.server.util;

import java.io.UnsupportedEncodingException;

import org.apache.thrift.TBase;
import org.apache.thrift.TException;

import com.m2u.eyelink.collector.util.AgentEventType;
import com.m2u.eyelink.thrift.HeaderTBaseSerializer;
import com.m2u.eyelink.thrift.SerializationUtils;
import com.m2u.eyelink.thrift.SerializerFactory;
import com.m2u.eyelink.util.BytesUtils;

public class AgentEventMessageSerializer {

    private static final byte[] EMPTY_BYTES = new byte[0];

    private final SerializerFactory<HeaderTBaseSerializer> tBaseSerializerFactory;

    public AgentEventMessageSerializer(SerializerFactory<HeaderTBaseSerializer> tBaseSerializerFactory) {
        this.tBaseSerializerFactory = tBaseSerializerFactory;
    }

    public byte[] serialize(AgentEventType agentEventType, Object eventMessage) throws UnsupportedEncodingException {
        if (agentEventType == null) {
            throw new NullPointerException("agentEventType must not be null");
        }

        Class<?> eventMessageType = agentEventType.getMessageType();
        if (eventMessageType == Void.class) {
            return EMPTY_BYTES;
        } else {
            if (eventMessage == null) {
                throw new NullPointerException("eventMessage of type [" + eventMessageType.getName() + "] expected, but was null");
            }
        }

        if (!eventMessageType.isAssignableFrom(eventMessage.getClass())) {
            throw new IllegalArgumentException("Unexpected eventMessage of type [" + eventMessage.getClass().getName() + "] received. Expected : ["
                    + eventMessageType.getClass().getName() + "]");
        }

        if (eventMessage instanceof TBase) {
            try {
                return SerializationUtils.serialize((TBase<?, ?>)eventMessage, this.tBaseSerializerFactory);
            } catch (TException e) {
                throw new UnsupportedEncodingException(e.getMessage());
            }
        } else if (eventMessage instanceof String) {
            return BytesUtils.toBytes((String)eventMessage);
        }
        throw new UnsupportedEncodingException("Unsupported event message type [" + eventMessage.getClass().getName() + "]");
    }
}
