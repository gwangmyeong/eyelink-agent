package com.m2u.eyelink.sender;


public interface Future<T> {

    T getResult();

    Throwable getCause();

    boolean isReady();

    boolean isSuccess();

    boolean setListener(FutureListener<T> listener);

    boolean await(long timeoutMillis);

    boolean await();
}
