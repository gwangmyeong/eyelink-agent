package com.m2u.eyelink.collector.cluster.zookeeper.exception;


public class ConnectionException extends ELAgentZookeeperException {

    public ConnectionException() {
    }

    public ConnectionException(String message) {
        super(message);
    }

    public ConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConnectionException(Throwable cause) {
        super(cause);
    }

}
