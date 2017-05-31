package com.m2u.eyelink.agent.profiler.context.id;

import com.google.inject.Inject;
import com.m2u.eyelink.agent.profiler.context.module.AgentId;
import com.m2u.eyelink.agent.profiler.context.module.AgentStartTime;
import com.m2u.eyelink.context.DefaultTraceId;
import com.m2u.eyelink.context.TraceId;
import com.m2u.eyelink.util.TransactionId;
import com.m2u.eyelink.util.TransactionIdUtils;

public class DefaultTraceIdFactory implements TraceIdFactory {

    private final String agentId;
    private final long agentStartTime;
    private final IdGenerator idGenerator;

    @Inject
    public DefaultTraceIdFactory(@AgentId String agentId, @AgentStartTime long agentStartTime, IdGenerator idGenerator) {
        if (agentId == null) {
            throw new NullPointerException("agentId must not be null");
        }
        if (idGenerator == null) {
            throw new NullPointerException("idGenerator must not be null");
        }
        this.agentId = agentId;
        this.agentStartTime = agentStartTime;
        this.idGenerator = idGenerator;
    }

    @Override
    public TraceId newTraceId() {
        final long localTransactionId = idGenerator.nextTransactionId();
        final TraceId traceId = new DefaultTraceId(agentId, agentStartTime, localTransactionId);
        return traceId;
    }

    public TraceId parse(String transactionId, long parentSpanId, long spanId, short flags) {
        if (transactionId == null) {
            throw new NullPointerException("transactionId must not be null");
        }
        final TransactionId parseId = TransactionIdUtils.parseTransactionId(transactionId);
        return new DefaultTraceId(parseId.getAgentId(), parseId.getAgentStartTime(), parseId.getTransactionSequence(), parentSpanId, spanId, flags);
    }
}
