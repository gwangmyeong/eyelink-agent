package com.m2u.eyelink.util;

import java.util.Random;

import com.m2u.eyelink.logging.PLogger;
import com.m2u.eyelink.logging.PLoggerFactory;
import com.m2u.eyelink.util.jdk.ELAgentThreadLocalRandomFactory;
import com.m2u.eyelink.util.jdk.JvmUtils;
import com.m2u.eyelink.util.jdk.JvmVersion;
import com.m2u.eyelink.util.jdk.ThreadLocalRandomFactory;

public class ThreadLocalRandomUtils {

    private static final PLogger LOGGER = PLoggerFactory.getLogger(ThreadLocalRandomUtils.class);

    private static final ThreadLocalRandomFactory THREAD_LOCAL_RANDOM_FACTORY = createThreadLocalRandomFactory();

    // Jdk 7+
    private static final String DEFAULT_THREAD_LOCAL_RANDOM_FACTORY = "com.m2u.eyelink.util.jdk.JdkThreadLocalRandomFactory";

    private ThreadLocalRandomUtils() {
        throw new IllegalAccessError();
    }

    private static ThreadLocalRandomFactory createThreadLocalRandomFactory() {
        final JvmVersion jvmVersion = JvmUtils.getVersion();
        if (jvmVersion == JvmVersion.JAVA_6) {
            return new ELAgentThreadLocalRandomFactory();
        } else if (jvmVersion.onOrAfter(JvmVersion.JAVA_7)) {
            try {
                ClassLoader classLoader = ThreadLocalRandomUtils.class.getClassLoader();
                if (classLoader == null) {
                    classLoader = ClassLoader.getSystemClassLoader();
                }
                final Class<? extends ThreadLocalRandomFactory> threadLocalRandomFactoryClass =
                        (Class<? extends ThreadLocalRandomFactory>) Class.forName(DEFAULT_THREAD_LOCAL_RANDOM_FACTORY, true, classLoader);
                return threadLocalRandomFactoryClass.newInstance();
            } catch (ClassNotFoundException e) {
                logError(e);
            } catch (InstantiationException e) {
                logError(e);
            } catch (IllegalAccessException e) {
                logError(e);
            }
            return new ELAgentThreadLocalRandomFactory();
        } else {
            throw new RuntimeException("Unsupported jvm version " + jvmVersion);
        }

    }

    private static void logError(Exception e) {
        LOGGER.info("JdkThreadLocalRandomFactory not found.");
    }

    public static Random current() {
        return THREAD_LOCAL_RANDOM_FACTORY.current();
    }
}
