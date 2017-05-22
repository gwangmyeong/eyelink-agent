package com.m2u.eyelink.util;

public class Asserts {
    private Asserts() {}
    
    public static void notNull(Object value, String name) {
        if (value == null) {
            throw new NullPointerException(name + " can not be null");
        }
    }
}
