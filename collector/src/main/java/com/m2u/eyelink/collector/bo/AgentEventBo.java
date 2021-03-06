package com.m2u.eyelink.collector.bo;

import java.util.HashMap;
import java.util.Map;

import com.m2u.eyelink.collector.util.AgentEventType;
import com.m2u.eyelink.collector.util.TimeUtils;

public class AgentEventBo {

    public static final int CURRENT_VERSION = 0;

    private final byte version;
    private final String agentId;
    private final long startTimestamp;
    private final long eventTimestamp;
    private final AgentEventType eventType;
    private byte[] eventBody;

    public AgentEventBo(String agentId, long startTimestamp, long eventTimestamp, AgentEventType eventType) {
        this(CURRENT_VERSION, agentId, startTimestamp, eventTimestamp, eventType);
    }

    public AgentEventBo(int version, String agentId, long startTimestamp, long eventTimestamp, AgentEventType eventType) {
        if (version < 0 || version > 255) {
            throw new IllegalArgumentException("version out of range (0~255)");
        }
        if (agentId == null) {
            throw new IllegalArgumentException("agentId cannot be null");
        }
        if (agentId.isEmpty()) {
            throw new IllegalArgumentException("agentId cannot be empty");
        }
        if (startTimestamp < 0) {
            throw new IllegalArgumentException("startTimestamp cannot be less than 0");
        }
        if (eventTimestamp < 0) {
            throw new IllegalArgumentException("eventTimestamp cannot be less than 0");
        }
        if (eventType == null) {
            throw new IllegalArgumentException("agentEventType cannot be null");
        }
        this.version = (byte)(version & 0xFF);
        this.agentId = agentId;
        this.startTimestamp = startTimestamp;
        this.eventTimestamp = eventTimestamp;
        this.eventType = eventType;
    }

    public int getVersion() {
        return this.version & 0xFF;
    }

    public String getAgentId() {
        return agentId;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public long getEventTimestamp() {
        return eventTimestamp;
    }

    public AgentEventType getEventType() {
        return eventType;
    }

    public byte[] getEventBody() {
        return eventBody;
    }

    public void setEventBody(byte[] eventBody) {
        this.eventBody = eventBody;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((agentId == null) ? 0 : agentId.hashCode());
        result = prime * result + (int)(eventTimestamp ^ (eventTimestamp >>> 32));
        result = prime * result + ((eventType == null) ? 0 : eventType.hashCode());
        result = prime * result + (int)(startTimestamp ^ (startTimestamp >>> 32));
        result = prime * result + version;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AgentEventBo other = (AgentEventBo)obj;
        if (agentId == null) {
            if (other.agentId != null)
                return false;
        } else if (!agentId.equals(other.agentId))
            return false;
        if (eventTimestamp != other.eventTimestamp)
            return false;
        if (eventType != other.eventType)
            return false;
        if (startTimestamp != other.startTimestamp)
            return false;
        if (version != other.version)
            return false;
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AgentEventBo{");
        sb.append("version=").append(this.version);
        sb.append(", agentId='").append(this.agentId).append('\'');
        sb.append(", startTimestamp=").append(this.startTimestamp);
        sb.append(", eventTimestamp=").append(this.eventTimestamp);
        sb.append(", eventType='").append(this.getEventType().getDesc()).append('\'');
        return sb.toString();
    }

	public Map<String, Object> getMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("version", this.version);
		map.put("agentId", this.agentId);
		map.put("startTimestamp", TimeUtils.convertEpochToDate(this.startTimestamp));
		map.put("eventTimestamp", TimeUtils.convertEpochToDate(this.eventTimestamp));
		map.put("eventType", this.getEventType().getDesc());
		return map;
	}

}
