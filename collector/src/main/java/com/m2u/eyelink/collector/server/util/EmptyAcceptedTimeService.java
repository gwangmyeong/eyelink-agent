package com.m2u.eyelink.collector.server.util;

public class EmptyAcceptedTimeService implements AcceptedTimeService{
    private final long acceptedTime;

    public EmptyAcceptedTimeService() {
        this(0);
    }

    public EmptyAcceptedTimeService(long acceptedTime) {
        this.acceptedTime = acceptedTime;
    }

    public void accept() {

    }

    public void accept(long time) {

    }

    public long getAcceptedTime() {
        return acceptedTime;
    }
}
