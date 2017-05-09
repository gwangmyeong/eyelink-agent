package com.m2u.eyelink.util;

import java.util.Map;

public final class IntHashMapUtils {

    private IntHashMapUtils() {
    }

    public static <V> IntHashMap<V> copy(Map<Integer, V> target) {
        if (target == null) {
            throw new NullPointerException("target must not be null");
        }
        final IntHashMap<V> copyMap = new IntHashMap<V>();
        for (Map.Entry<Integer, V> entry : target.entrySet()) {
            copyMap.put(entry.getKey(), entry.getValue());
        }
        return copyMap;
    }
}
