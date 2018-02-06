package com.m2u.eyelink.agent.profiler.context;

import java.util.List;

import com.google.inject.Inject;
import com.m2u.eyelink.agent.profiler.context.module.AgentId;
import com.m2u.eyelink.agent.profiler.context.module.AgentStartTime;
import com.m2u.eyelink.agent.profiler.context.module.ApplicationName;
import com.m2u.eyelink.agent.profiler.context.module.ApplicationServerType;
import com.m2u.eyelink.common.trace.ServiceType;

public class DefaultSpanChunkFactory implements SpanChunkFactory {
    private final String applicationName;
    private final String agentId;
    private final long agentStartTime;
    private final ServiceType applicationServiceType;

    @Inject
    public DefaultSpanChunkFactory(@ApplicationName String applicationName, @AgentId String agentId, @AgentStartTime long agentStartTime,
                            @ApplicationServerType ServiceType applicationServiceType) {

        if (applicationName == null) {
            throw new NullPointerException("applicationName must not be null");
        }
        if (agentId == null) {
            throw new NullPointerException("agentId must not be null");
        }
        if (applicationServiceType == null) {
            throw new NullPointerException("applicationServiceType must not be null");
        }

        this.applicationName = applicationName;
        this.agentId = agentId;
        this.agentStartTime = agentStartTime;
        this.applicationServiceType = applicationServiceType;
    }

    @Override
    public SpanChunk create(final List<SpanEvent> flushData) {
        if (flushData == null) {
            throw new NullPointerException("flushData must not be null");
        }
        // TODO must be equals to or greater than 1
        final int size = flushData.size();
        if (size < 1) {
            throw new IllegalArgumentException("flushData.size() < 1 size:" + size);
        }


        final SpanEvent first = flushData.get(0);
        if (first == null) {
            throw new IllegalStateException("first SpanEvent is null");
        }
        final Span parentSpan = first.getSpan();

        final SpanChunk spanChunk = new SpanChunk(flushData);
        spanChunk.setAgentId(agentId);
        spanChunk.setApplicationName(applicationName);
        spanChunk.setAgentStartTime(agentStartTime);
        spanChunk.setApplicationServiceType(applicationServiceType.getCode());

        spanChunk.setServiceType(parentSpan.getServiceType());


        final byte[] transactionId = parentSpan.getTransactionId();
        spanChunk.setTransactionId(transactionId);


        spanChunk.setSpanId(parentSpan.getSpanId());

        spanChunk.setEndPoint(parentSpan.getEndPoint());
        return spanChunk;
    }
}
