package com.m2u.eyelink.collector.bo.serializer.stat;

import com.m2u.eyelink.collector.bo.stat.AgentStatType;

public class AgentStatRowKeyComponent {

    private final String agentId;
    private final AgentStatType agentStatType;
    private final long baseTimestamp;

    public AgentStatRowKeyComponent(String agentId, AgentStatType agentStatType, long baseTimestamp) {
        this.agentId = agentId;
        this.agentStatType = agentStatType;
        this.baseTimestamp = baseTimestamp;
    }

    public String getAgentId() {
        return this.agentId;
    }

    public AgentStatType getAgentStatType() {
        return this.agentStatType;
    }

    public long getBaseTimestamp() {
        return this.baseTimestamp;
    }
}
