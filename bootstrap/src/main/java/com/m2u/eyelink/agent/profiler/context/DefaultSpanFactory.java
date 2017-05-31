package com.m2u.eyelink.agent.profiler.context;

import com.google.inject.Inject;
import com.m2u.eyelink.agent.profiler.context.module.AgentId;
import com.m2u.eyelink.agent.profiler.context.module.AgentStartTime;
import com.m2u.eyelink.agent.profiler.context.module.ApplicationName;
import com.m2u.eyelink.agent.profiler.context.module.ApplicationServerType;
import com.m2u.eyelink.context.Span;
import com.m2u.eyelink.trace.ServiceType;

public class DefaultSpanFactory implements SpanFactory {

    private final String applicationName;
    private final String agentId;
    private final long agentStartTime;
    private final ServiceType applicationServiceType;

    @Inject
    public DefaultSpanFactory(@ApplicationName String applicationName, @AgentId String agentId, @AgentStartTime long agentStartTime,
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
    public Span newSpan() {
        final Span span = new Span();
        span.setAgentId(agentId);
        span.setApplicationName(applicationName);
        span.setAgentStartTime(agentStartTime);
        span.setApplicationServiceType(applicationServiceType.getCode());
        span.markBeforeTime();
        return span;
    }

}
