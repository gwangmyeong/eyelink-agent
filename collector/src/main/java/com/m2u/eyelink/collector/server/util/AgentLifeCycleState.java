package com.m2u.eyelink.collector.server.util;

public enum AgentLifeCycleState {
    RUNNING((short)100, "Running"),
    SHUTDOWN((short)200, "Shutdown"),
    UNEXPECTED_SHUTDOWN((short)201, "Unexpected Shutdown"),
    DISCONNECTED((short)300, "Disconnected"),
    UNKNOWN((short)-1, "Unknown");

    private final short code; 
    private final String desc;

    AgentLifeCycleState(short code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public short getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }

    @Override
    public String toString() {
        return this.desc;
    }
    
    public static AgentLifeCycleState getStateByCode(short code) {
        for (AgentLifeCycleState state : AgentLifeCycleState.values()) {
            if (state.code == code) {
                return state;
            }
        }
        return UNKNOWN;
    }
}