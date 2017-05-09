package com.m2u.eyelink.agent;

public class ProfilerLibClass implements LibClass {

    private static final String[] PINPOINT_PROFILER_CLASS = new String[] {
            "com.navercorp.pinpoint.profiler",
            "com.navercorp.pinpoint.thrift",
            "com.navercorp.pinpoint.rpc",
            /*
             * @deprecated javassist
             */
            "javassist",
            "org.objectweb.asm",
            "org.slf4j",
            "org.apache.thrift",
            "org.jboss.netty",
            "com.google.common",
            "org.apache.commons.lang",
            "org.apache.log4j",
            "com.codahale.metrics",
            "com.nhncorp.nelo2"
    };

    @Override
    public boolean onLoadClass(String clazzName) {
        final int length = PINPOINT_PROFILER_CLASS.length;
        for (int i = 0; i < length; i++) {
            if (clazzName.startsWith(PINPOINT_PROFILER_CLASS[i])) {
                return ON_LOAD_CLASS;
            }
        }
        return DELEGATE_PARENT;
    }
}
