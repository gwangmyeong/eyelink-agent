package com.m2u.eyelink.collector.cluster.zookeeper.exception;

public class NoNodeException extends ELAgentZookeeperException {

    public NoNodeException() {
    }

    public NoNodeException(String message) {
        super(message);
    }

    public NoNodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoNodeException(Throwable cause) {
        super(cause);
    } 

}
