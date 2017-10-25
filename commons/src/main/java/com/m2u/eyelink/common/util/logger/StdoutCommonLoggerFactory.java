package com.m2u.eyelink.common.util.logger;

import com.m2u.eyelink.common.util.logger.CommonLogger;
import com.m2u.eyelink.common.util.logger.CommonLoggerFactory;
import com.m2u.eyelink.common.util.logger.StdoutCommonLogger;
import com.m2u.eyelink.common.util.logger.StdoutCommonLoggerFactory;

public class StdoutCommonLoggerFactory implements CommonLoggerFactory {
    public static final CommonLoggerFactory INSTANCE = new StdoutCommonLoggerFactory();

    static {
        setup();
    }

    private static void setup() {
        // TODO setup stdout logger
    }

    public CommonLogger getLogger(String loggerName) {
        return new StdoutCommonLogger(loggerName);
    }
}
