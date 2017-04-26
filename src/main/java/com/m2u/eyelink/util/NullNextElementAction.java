package com.m2u.eyelink.util;


public class NullNextElementAction<E> implements NextElementAction<E> {
    @Override
    public E nextElement() {
        return null;
    }
}
