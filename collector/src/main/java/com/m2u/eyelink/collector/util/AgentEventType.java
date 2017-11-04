package com.m2u.eyelink.collector.util;

import static com.m2u.eyelink.collector.util.AgentEventTypeCategory.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.m2u.eyelink.thrift.TCommandThreadDumpResponse; 

public enum AgentEventType {
    AGENT_CONNECTED(10100, "Agent connected", Void.class, DURATIONAL, AGENT_LIFECYCLE),
    AGENT_PING(10199, "Agent ping", Void.class, AGENT_LIFECYCLE),
    AGENT_SHUTDOWN(10200, "Agent shutdown", Void.class, DURATIONAL, AGENT_LIFECYCLE),
    AGENT_UNEXPECTED_SHUTDOWN(10201, "Agent unexpected shutdown", Void.class, DURATIONAL, AGENT_LIFECYCLE),
    AGENT_CLOSED_BY_SERVER(10300, "Agent connection closed by server", Void.class, DURATIONAL, AGENT_LIFECYCLE),
    AGENT_UNEXPECTED_CLOSE_BY_SERVER(10301, "Agent connection unexpectedly closed by server", Void.class, DURATIONAL, AGENT_LIFECYCLE),
    USER_THREAD_DUMP(20100, "Thread dump by user", TCommandThreadDumpResponse.class, USER_REQUEST, THREAD_DUMP),
    OTHER(-1, "Other event", String.class, AgentEventTypeCategory.OTHER);
    
    private final int code;
    private final String desc;
    private final Class<?> messageType;
    private final Set<AgentEventTypeCategory> category;

    AgentEventType(int code, String desc, Class<?> messageType, AgentEventTypeCategory... category) {
        this.code = code;
        this.desc = desc;
        this.messageType = messageType;
        if (category == null || category.length == 0) {
            this.category = Collections.emptySet();
        } else {
            this.category = new HashSet<AgentEventTypeCategory>(Arrays.asList(category));
        }
    }
    
    public int getCode() {
        return this.code;
    }

    public String getDesc() {
        return desc;
    }
    
    public Class<?> getMessageType() {
        return this.messageType;
    }
    
    public Set<AgentEventTypeCategory> getCategory() {
        return Collections.unmodifiableSet(this.category);
    }
    
    public boolean isCategorizedAs(AgentEventTypeCategory category) {
        return this.category.contains(category);
    }

    @Override
    public String toString() {
        return desc;
    }
    
    public static AgentEventType getTypeByCode(int code) {
        for (AgentEventType eventType : AgentEventType.values()) {
            if (eventType.code == code) {
                return eventType;
            }
        }
        return null;
    }
    
    public static Set<AgentEventType> getTypesByCatgory(AgentEventTypeCategory category) {
        Set<AgentEventType> eventTypes = new HashSet<AgentEventType>();
        for (AgentEventType eventType : AgentEventType.values()) {
            if (eventType.category.contains(category)) {
                eventTypes.add(eventType);
            }
        }
        return eventTypes;
    }
}
