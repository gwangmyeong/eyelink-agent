package com.m2u.eyelink.agent.profiler.instrument;

import java.util.Arrays;
import java.util.List;

public class BootstrapPackage {

    private static final List<String> BOOTSTRAP_PACKAGE_LIST = Arrays.asList("com.m2u.eyelink.agent", "com.m2u.eyelink.common", "com.m2u.eyelink.exception");

    public boolean isBootstrapPackage(String className) {
        for (String bootstrapPackage : BOOTSTRAP_PACKAGE_LIST) {
            if (className.startsWith(bootstrapPackage)) {
                return true;
            }
        }
        return false;
    }
}
