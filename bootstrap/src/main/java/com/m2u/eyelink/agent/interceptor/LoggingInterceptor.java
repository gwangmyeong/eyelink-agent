package com.m2u.eyelink.agent.interceptor;

import java.util.Arrays;

import com.m2u.eyelink.logging.CommonLogger;
import com.m2u.eyelink.logging.StdoutCommonLoggerFactory;

public class LoggingInterceptor implements StaticAroundInterceptor,
		AroundInterceptor, AroundInterceptor0, AroundInterceptor1,
		AroundInterceptor2, AroundInterceptor3, AroundInterceptor4,
		AroundInterceptor5, ApiIdAwareAroundInterceptor {

	private final CommonLogger logger;

	public LoggingInterceptor(String loggerName) {
		this.logger = StdoutCommonLoggerFactory.INSTANCE.getLogger(loggerName);
	}

	@Override
	public void before(Object target, String className, String methodName,
			String parameterDescription, Object[] args) {
		if (logger.isTraceEnabled()) {
			logger.trace("before " + defaultString(target) + " " + className
					+ "." + methodName + parameterDescription + " args:"
					+ Arrays.toString(args));
		}
	}

	@Override
	public void after(Object target, String className, String methodName,
			String parameterDescription, Object[] args, Object result,
			Throwable throwable) {
		if (logger.isTraceEnabled()) {
			logger.trace("after " + defaultString(target) + " " + className
					+ "." + methodName + parameterDescription + " args:"
					+ Arrays.toString(args) + " result:" + result
					+ " Throwable:" + throwable);
		}
	}

	@Override
	public void before(Object target, Object[] args) {
		if (logger.isTraceEnabled()) {
			logger.trace("before " + defaultString(target) + " args:"
					+ Arrays.toString(args));
		}
	}

	@Override
	public void after(Object target, Object[] args, Object result,
			Throwable throwable) {
		if (logger.isTraceEnabled()) {
			logger.trace("after " + defaultString(target) + " args:"
					+ Arrays.toString(args) + " result:" + result
					+ " Throwable:" + throwable);
		}
	}

	public static String defaultString(final Object object) {
		return String.valueOf(object);
	}

	@Override
	public void before(Object target, int apiId, Object[] args) {
	}

	@Override
	public void after(Object target, int apiId, Object[] args, Object result,
			Throwable throwable) {

	}

	@Override
	public void before(Object target) {

	}

	@Override
	public void after(Object target, Object result, Throwable throwable) {

	}

	@Override
	public void before(Object target, Object arg0) {

	}

	@Override
	public void after(Object target, Object arg0, Object result,
			Throwable throwable) {

	}

	@Override
	public void before(Object target, Object arg0, Object arg1) {

	}

	@Override
	public void after(Object target, Object arg0, Object arg1, Object result,
			Throwable throwable) {

	}

	@Override
	public void before(Object target, Object arg0, Object arg1, Object arg2) {

	}

	@Override
	public void after(Object target, Object arg0, Object arg1, Object arg2,
			Object result, Throwable throwable) {

	}

	@Override
	public void before(Object target, Object arg0, Object arg1, Object arg2,
			Object arg3) {

	}

	@Override
	public void after(Object target, Object arg0, Object arg1, Object arg2,
			Object arg3, Object result, Throwable throwable) {

	}

	@Override
	public void before(Object target, Object arg0, Object arg1, Object arg2,
			Object arg3, Object arg4) {

	}

	@Override
	public void after(Object target, Object arg0, Object arg1, Object arg2,
			Object arg3, Object arg4, Object result, Throwable throwable) {

	}
}
