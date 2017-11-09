package com.m2u.eyelink.rpc;

import com.m2u.eyelink.rpc.packet.SendPacket;

public interface MessageListener {

    void handleSend(SendPacket sendPacket, ELAgentSocket elagentSocket);

    void handleRequest(RequestPacket requestPacket, ELAgentSocket elagentSocket);

}
