package com.m2u.eyelink.agent;

public class ProfilerLibClass implements LibClass {

	// TODO bootClass가 일치하면 classloading 하지 않음.
    private static final String[] ELAGENT_PROFILER_CLASS = new String[] {
    	// TODO 일단 테스트를 위해서 com.m2u.eyelink.context 전체를 설정함. 추후 loading Library 관리를 위해서 com.m2u.eyelink.context.thrift 만 처리해야 하는지 확인 필요함.
            "com.m2u.eyelink.agent.profiler",
            "com.m2u.eyelink.context",
            "com.m2u.eyelink.rpc",
            /*
             * @deprecated javassist
             */
            "javassist",
            "org.objectweb.asm",
            "org.slf4j",
            "org.apache.thrift",
            "org.jboss.netty",
            "com.google.common",
            // google guice
            "com.google.inject",
            "org.aopalliance",            
            "org.apache.commons.lang",
            "org.apache.log4j",
            "com.codahale.metrics",
            "com.m2u.eyelink.nelo2"
    };

    @Override
    public boolean onLoadClass(String clazzName) {
        final int length = ELAGENT_PROFILER_CLASS.length;
        for (int i = 0; i < length; i++) {
            if (clazzName.startsWith(ELAGENT_PROFILER_CLASS[i])) {
                return ON_LOAD_CLASS;
            }
        }
        return DELEGATE_PARENT;
    }
}
