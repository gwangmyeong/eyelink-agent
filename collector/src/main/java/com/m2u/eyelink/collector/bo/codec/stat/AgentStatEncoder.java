package com.m2u.eyelink.collector.bo.codec.stat;

import java.nio.ByteBuffer;
import java.util.List;

import com.m2u.eyelink.collector.bo.stat.AgentStatDataPoint;
import com.m2u.eyelink.util.AutomaticBuffer;
import com.m2u.eyelink.util.Buffer;

public class AgentStatEncoder<T extends AgentStatDataPoint> {

    private final AgentStatCodec<T> codec;

    public AgentStatEncoder(AgentStatCodec<T> codec) {
        this.codec = codec;
    }

    public ByteBuffer encodeQualifier(long timestampDelta) {
        // Variable-length encoding of 5 minutes (300000 ms) takes up max 3 bytes
        Buffer qualifierBuffer = new AutomaticBuffer(3);
        qualifierBuffer.putVLong(timestampDelta);
        return qualifierBuffer.wrapByteBuffer();
    }

    public ByteBuffer encodeValue(List<T> agentStatDataPoints) {
        Buffer valueBuffer = new AutomaticBuffer();
        valueBuffer.putByte(this.codec.getVersion());
        codec.encodeValues(valueBuffer, agentStatDataPoints);
        return valueBuffer.wrapByteBuffer();
    }
}
