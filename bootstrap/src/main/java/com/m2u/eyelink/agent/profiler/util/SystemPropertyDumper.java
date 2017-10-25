package com.m2u.eyelink.agent.profiler.util;

import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemPropertyDumper {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void dump() {
        if (logger.isInfoEnabled()) {
            Properties properties = System.getProperties();
            Set<String> strings = properties.stringPropertyNames();
            for (String key : strings) {
                logger.info("SystemProperties {}={}", key, properties.get(key));
            }
        }
    }
}
