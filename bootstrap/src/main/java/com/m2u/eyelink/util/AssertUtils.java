package com.m2u.eyelink.util;

public class AssertUtils {

    private AssertUtils() {
    }

    public static void assertNotNull(Object object) {
        assertNotNull(object, "Object may not be null.");
    }

    public static void assertNotNull(Object object, String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
    }

    public static void assertTrue(boolean value) {
        assertTrue(value, "value must be true.");
    }

    public static void assertTrue(boolean value, String message) {
        if (!value) {
            throw new IllegalArgumentException(message);
        }
    }
}
