package com.m2u.eyelink.collector.cluster.zookeeper.exception;

public class UnknownException extends ELAgentZookeeperException {

    public UnknownException() {
    }

    public UnknownException(String message) {
        super(message);
    }

    public UnknownException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownException(Throwable cause) {
        super(cause);
    }

}
