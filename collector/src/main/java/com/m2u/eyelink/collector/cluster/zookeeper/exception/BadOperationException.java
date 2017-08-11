package com.m2u.eyelink.collector.cluster.zookeeper.exception;

public class BadOperationException extends ELAgentZookeeperException {

    public BadOperationException() {
    }

    public BadOperationException(String message) {
        super(message);
    }

    public BadOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadOperationException(Throwable cause) {
        super(cause);
    }

}
