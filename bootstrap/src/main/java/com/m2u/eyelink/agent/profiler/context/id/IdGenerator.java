package com.m2u.eyelink.agent.profiler.context.id;


public interface IdGenerator {


    long nextTransactionId();

    long nextContinuedTransactionId();

    long nextDisabledId();

    long nextContinuedDisabledId();

    long currentTransactionId();

    long currentContinuedTransactionId();

    long currentDisabledId();

    long currentContinuedDisabledId();
}
