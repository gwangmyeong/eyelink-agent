package com.m2u.eyelink.collector.bo.serializer.stat;

public class AgentStatDecodingContext {

    private String agentId;

    private long baseTimestamp;

    private long timestampDelta;

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public long getBaseTimestamp() {
        return baseTimestamp;
    }

    public void setBaseTimestamp(long baseTimestamp) {
        this.baseTimestamp = baseTimestamp;
    }

    public long getTimestampDelta() {
        return timestampDelta;
    }

    public void setTimestampDelta(long timestampDelta) {
        this.timestampDelta = timestampDelta;
    }
}
