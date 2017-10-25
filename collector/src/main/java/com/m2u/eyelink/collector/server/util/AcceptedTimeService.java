package com.m2u.eyelink.collector.server.util;

public interface AcceptedTimeService {

    void accept();

    void accept(long time);

    long getAcceptedTime();
}