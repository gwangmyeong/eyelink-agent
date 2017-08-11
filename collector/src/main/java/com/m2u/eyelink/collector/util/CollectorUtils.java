package com.m2u.eyelink.collector.util;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

public final class CollectorUtils {
    private static final RuntimeMXBean RUNTIME_MXBEAN = ManagementFactory.getRuntimeMXBean();

    private CollectorUtils() {
    }

    public static String getServerIdentifier() {
        // if the return value is not unique, it will be changed to MAC address or IP address.
        // It means that the return value has format of "pid@hostname" (it is possible to be duplicate for "localhost")
        return RUNTIME_MXBEAN.getName();
    }

}
