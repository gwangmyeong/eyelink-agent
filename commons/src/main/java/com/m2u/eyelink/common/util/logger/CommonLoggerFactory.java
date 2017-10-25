package com.m2u.eyelink.common.util.logger;

import com.m2u.eyelink.common.util.logger.CommonLogger;

public interface CommonLoggerFactory {
    CommonLogger getLogger(String loggerName);
}
