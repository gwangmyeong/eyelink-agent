package com.m2u.eyelink.sender;


public interface FutureListener<T> {
    void onComplete(Future<T> future);
}
