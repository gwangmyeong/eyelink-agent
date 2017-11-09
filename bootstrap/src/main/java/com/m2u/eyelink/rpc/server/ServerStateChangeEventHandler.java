package com.m2u.eyelink.rpc.server;

import com.m2u.eyelink.rpc.SocketStateCode;

public interface ServerStateChangeEventHandler {
    void eventPerformed(ELAgentServer elagentServer, SocketStateCode stateCode) throws Exception;
    
    void exceptionCaught(ELAgentServer elagentServer, SocketStateCode stateCode, Throwable e);

}
