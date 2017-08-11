package com.m2u.eyelink.collector.cluster;

import java.util.concurrent.atomic.AtomicReference;

public class CommonStateContext {

    private final AtomicReference<CommonState> currentState = new AtomicReference<CommonState>();

    public CommonStateContext() {
        currentState.set(CommonState.NEW);
    }

    public CommonState getCurrentState() {
        return currentState.get();
    }

    public boolean changeStateInitializing() {
        return currentState.compareAndSet(CommonState.NEW, CommonState.INITIALIZING);
    }

    public boolean changeStateStarted() {
        return currentState.compareAndSet(CommonState.INITIALIZING, CommonState.STARTED);
    }

    public boolean changeStateDestroying() {
        return currentState.compareAndSet(CommonState.STARTED, CommonState.DESTROYING);
    }

    public boolean changeStateStopped() {
        return currentState.compareAndSet(CommonState.DESTROYING, CommonState.STOPPED);
    }

    public boolean changeStateIllegal() {
        currentState.set(CommonState.ILLEGAL_STATE);
        return true;
    }

    public boolean isStarted() {
        return currentState.get() == CommonState.STARTED;
    }
}
