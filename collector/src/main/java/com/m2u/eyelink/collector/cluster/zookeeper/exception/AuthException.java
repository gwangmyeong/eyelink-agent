package com.m2u.eyelink.collector.cluster.zookeeper.exception;

public class AuthException extends ELAgentZookeeperException {

    public AuthException() {
    }

    public AuthException(String message) {
        super(message);
    }

    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthException(Throwable cause) {
        super(cause);
    }

}
