package com.m2u.eyelink.agent.interceptor.registry;

public interface Locker {
    boolean lock(Object lock);

    boolean unlock(Object lock);
}
