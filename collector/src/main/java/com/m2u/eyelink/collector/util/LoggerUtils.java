package com.m2u.eyelink.collector.util;

import org.slf4j.Logger;
import org.slf4j.spi.LocationAwareLogger;

public final class LoggerUtils {
    // level : 00
    public static final int TRACE_LEVEL = LocationAwareLogger.TRACE_INT;
    // level : 10
    public static final int DEBUG_LEVEL  = LocationAwareLogger.DEBUG_INT;
    // level : 20
    public static final int INFO_LEVEL  = LocationAwareLogger.INFO_INT;
    // level : 30
    public static final int WARN_LEVEL  = LocationAwareLogger.WARN_INT;
    // level : 40
    public static final int ERROR_LEVEL  = LocationAwareLogger.ERROR_INT;
    // level : 50
    public static final int DISABLE_LEVEL  = LocationAwareLogger.ERROR_INT + 10;

    private LoggerUtils() {
    }

    public static int getLoggerLevel(Logger logger) {
        if (logger == null) {
            throw new NullPointerException("logger must not be null");
        }
        if (logger.isTraceEnabled()) {
            return TRACE_LEVEL;
        }
        if (logger.isDebugEnabled()) {
            return DEBUG_LEVEL;
        }
        if (logger.isInfoEnabled()) {
            return INFO_LEVEL;
        }
        if (logger.isWarnEnabled()) {
            return WARN_LEVEL;
        }
        if (logger.isErrorEnabled()) {
            return ERROR_LEVEL;
        }
        return DISABLE_LEVEL;
    }
}
