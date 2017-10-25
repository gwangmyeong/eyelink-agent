package com.m2u.eyelink.logging;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;


public class ELLogger {
	// private static final int LOG_LEVEL;
	// private final String loggerName;
	private final String messagePattern;
	private final PrintStream out;
	private final PrintStream err;

    public ELLogger(String loggerName) {
        this(loggerName, System.out, System.err);
    }

    // for test
    ELLogger(String loggerName, PrintStream out, PrintStream err) {
        if (loggerName == null) {
            throw new NullPointerException("loggerName must not be null");
        }
//        this.loggerName = loggerName;
        this.messagePattern = "{0,date,yyyy-MM-dd HH:mm:ss} [{1}](" + loggerName + ") {2}{3}";
        this.out = out;
        this.err = err;
    }
    
    public static ELLogger getLogger(String loggerName) {
        return new ELLogger(loggerName);
    }

    private String format(String logLevel, String msg, String exceptionMessage) {
        exceptionMessage = defaultString(exceptionMessage, "");

        MessageFormat messageFormat = new MessageFormat(messagePattern);
        final long date = System.currentTimeMillis();
        Object[] parameter = {date, logLevel, msg, exceptionMessage};
        return messageFormat.format(parameter);
    }

    public boolean isInfoEnabled() {
        return true;
    }

    public void info(String msg) {
        String formatMessage = format("INFO ", msg, "");
        this.out.println(formatMessage);
    }


    public boolean isWarnEnabled() {
        return true;
    }

    public void warn(String msg) {
        warn(msg, null);
    }

    public void warn(String msg, Throwable throwable) {
        String exceptionMessage = toString(throwable);
        String formatMessage = format("WARN ", msg, exceptionMessage);
        this.err.println(formatMessage);
    }

    private String toString(Throwable throwable) {
        if (throwable == null) {
            return "";
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println();
        throwable.printStackTrace(pw);
        pw.close();
        return sw.toString();
    }

    private String defaultString(String exceptionMessage, String defaultValue) {
        if (exceptionMessage == null) {
            return defaultValue;
        }
        return exceptionMessage;
    }

	public boolean isDebugEnabled() {
		// TODO Auto-generated method stub
		return true;
	}
}
