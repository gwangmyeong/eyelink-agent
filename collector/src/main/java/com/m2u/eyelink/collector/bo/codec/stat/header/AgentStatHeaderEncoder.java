package com.m2u.eyelink.collector.bo.codec.stat.header;

public interface AgentStatHeaderEncoder {
    void addCode(int code);
    byte[] getHeader();
}
