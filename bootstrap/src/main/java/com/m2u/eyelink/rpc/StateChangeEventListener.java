package com.m2u.eyelink.rpc;

public interface StateChangeEventListener<S extends ELAgentSocket> {

    void eventPerformed(S elagentSocket, SocketStateCode stateCode) throws Exception;

    void exceptionCaught(S elagentSocket, SocketStateCode stateCode, Throwable e);

}
