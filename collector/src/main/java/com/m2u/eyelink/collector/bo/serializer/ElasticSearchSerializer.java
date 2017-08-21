package com.m2u.eyelink.collector.bo.serializer;

import com.m2u.eyelink.collector.common.elasticsearch.Put;

public interface ElasticSearchSerializer<T, M extends Put> {

    void serialize(T value, M mutation, SerializationContext context);

}
