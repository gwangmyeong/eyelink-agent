package com.m2u.eyelink.collector.bo.codec.stat;

import java.util.List;

import com.m2u.eyelink.collector.bo.serializer.stat.AgentStatDecodingContext;
import com.m2u.eyelink.collector.bo.stat.AgentStatDataPoint;
import com.m2u.eyelink.util.Buffer;

public interface AgentStatCodec<T extends AgentStatDataPoint> {

    byte getVersion();

    void encodeValues(Buffer valueBuffer, List<T> agentStatDataPoints);

    List<T> decodeValues(Buffer valueBuffer, AgentStatDecodingContext decodingContext);
}
