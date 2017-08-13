package com.m2u.eyelink.collector.bo.stat;

public enum AgentStatType {
    UNKNOWN(0, "Unknown"),
    JVM_GC(1, "JVM GC"),
    JVM_GC_DETAILED(2, "JVM GC Detailed"),
    CPU_LOAD(3, "Cpu Usage"),
    TRANSACTION((byte) 4, "Transaction"),
    ACTIVE_TRACE((byte) 5, "Active Trace"),
    DATASOURCE((byte) 6, "DataSource");

    public static final int TYPE_CODE_BYTE_LENGTH = 1;

    private final byte typeCode;
    private final String name;

    AgentStatType(int typeCode, String name) {
        if (typeCode < 0 || typeCode > 255) {
            throw new IllegalArgumentException("type code out of range (0~255)");
        }
        this.typeCode = (byte) (typeCode & 0xFF);
        this.name = name;
    }

    public int getTypeCode() {
        return this.typeCode & 0xFF;
    }

    public byte getRawTypeCode() {
        return typeCode;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static AgentStatType fromTypeCode(byte typeCode) {
        for (AgentStatType agentStatType : AgentStatType.values()) {
            if (agentStatType.typeCode == typeCode) {
                return agentStatType;
            }
        }
        return UNKNOWN;
    }
}
