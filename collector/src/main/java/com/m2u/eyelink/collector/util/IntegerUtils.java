package com.m2u.eyelink.collector.util;

public final class IntegerUtils {

    public static int compare(int x, int y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

}
