package com.m2u.eyelink.agent.profiler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.agent.Agent;
import com.m2u.eyelink.agent.AgentOption;
import com.m2u.eyelink.agent.ProductInfo;
import com.m2u.eyelink.agent.interceptor.InterceptorInvokerHelper;
import com.m2u.eyelink.agent.profiler.context.module.ApplicationContext;
import com.m2u.eyelink.agent.profiler.context.module.DefaultApplicationContext;
import com.m2u.eyelink.agent.profiler.interceptor.registry.DefaultInterceptorRegistryBinder;
import com.m2u.eyelink.agent.profiler.interceptor.registry.InterceptorRegistryBinder;
import com.m2u.eyelink.agent.profiler.util.SystemPropertyDumper;
import com.m2u.eyelink.common.service.ServiceTypeRegistryService;
import com.m2u.eyelink.config.ProfilerConfig;
import com.m2u.eyelink.logging.PLogger;
import com.m2u.eyelink.logging.PLoggerBinder;
import com.m2u.eyelink.logging.PLoggerFactory;
import com.m2u.eyelink.logging.Slf4jLoggerBinder;
import com.m2u.eyelink.rpc.ClassPreLoader;

public class DefaultAgent implements Agent {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PLoggerBinder binder;

    private final ProfilerConfig profilerConfig;

    private final  ApplicationContext applicationContext;


    private final Object agentStatusLock = new Object();
    private volatile AgentStatus agentStatus;

    private final InterceptorRegistryBinder interceptorRegistryBinder;
    private final ServiceTypeRegistryService serviceTypeRegistryService;
    

    static {
        // Preload classes related to pinpoint-rpc module.
        ClassPreLoader.preload();
    }

    public DefaultAgent(AgentOption agentOption) {
        this(agentOption, createInterceptorRegistry(agentOption));
    }

    public static InterceptorRegistryBinder createInterceptorRegistry(AgentOption agentOption) {
        final int interceptorSize = getInterceptorSize(agentOption);
        return new DefaultInterceptorRegistryBinder(interceptorSize);
    }

    private static int getInterceptorSize(AgentOption agentOption) {
        if (agentOption == null) {
            return DefaultInterceptorRegistryBinder.DEFAULT_MAX;
        }
        final ProfilerConfig profilerConfig = agentOption.getProfilerConfig();
        return profilerConfig.getInterceptorRegistrySize();
    }

    public DefaultAgent(AgentOption agentOption, final InterceptorRegistryBinder interceptorRegistryBinder) {
        if (agentOption == null) {
            throw new NullPointerException("agentOption must not be null");
        }
        if (agentOption.getInstrumentation() == null) {
            throw new NullPointerException("instrumentation must not be null");
        }
        if (agentOption.getProfilerConfig() == null) {
            throw new NullPointerException("profilerConfig must not be null");
        }
        if (agentOption.getServiceTypeRegistryService() == null) {
            throw new NullPointerException("serviceTypeRegistryService must not be null");
        }

        if (interceptorRegistryBinder == null) {
            throw new NullPointerException("interceptorRegistryBinder must not be null");
        }
        logger.info("AgentOption:{}", agentOption);

        this.binder = new Slf4jLoggerBinder();
        bindPLoggerFactory(this.binder);

        this.interceptorRegistryBinder = interceptorRegistryBinder;
        interceptorRegistryBinder.bind();
        this.serviceTypeRegistryService = agentOption.getServiceTypeRegistryService();

        dumpSystemProperties();
        dumpConfig(agentOption.getProfilerConfig());

        changeStatus(AgentStatus.INITIALIZING);

        this.profilerConfig = agentOption.getProfilerConfig();

        this.applicationContext = newApplicationContext(agentOption, interceptorRegistryBinder);

        
        InterceptorInvokerHelper.setPropagateException(profilerConfig.isPropagateInterceptorException());
    }

    protected ApplicationContext newApplicationContext(AgentOption agentOption, InterceptorRegistryBinder interceptorRegistryBinder) {
        return new DefaultApplicationContext(agentOption, interceptorRegistryBinder);
    }


    protected ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    private void dumpSystemProperties() {
        SystemPropertyDumper dumper = new SystemPropertyDumper();
        dumper.dump();
    }

    private void dumpConfig(ProfilerConfig profilerConfig) {
        if (logger.isInfoEnabled()) {
            logger.info("{}\n{}", "dumpConfig", profilerConfig);

        }
    }

    private void changeStatus(AgentStatus status) {
        this.agentStatus = status;
        if (logger.isDebugEnabled()) {
            logger.debug("Agent status is changed. {}", status);
        }
    }

    private void bindPLoggerFactory(PLoggerBinder binder) {
        final String binderClassName = binder.getClass().getName();
        PLogger pLogger = binder.getLogger(binder.getClass().getName());
        pLogger.info("PLoggerFactory.initialize() bind:{} cl:{}", binderClassName, binder.getClass().getClassLoader());
        // Set binder to static LoggerFactory
        // Should we unset binder at shutdown hook or stop()?
        PLoggerFactory.initialize(binder);
    }


    public ServiceTypeRegistryService getServiceTypeRegistryService() {
        return serviceTypeRegistryService;
    }
    
    @Override
    public void start() {
        synchronized (agentStatusLock) {
            if (this.agentStatus == AgentStatus.INITIALIZING) {
                changeStatus(AgentStatus.RUNNING);
            } else {
                logger.warn("Agent already started.");
                return;
            }
        }
        logger.info("Starting {} Agent.", ProductInfo.NAME);
        this.applicationContext.start();

    }

    @Override
    public void stop() {
        stop(false);
    }

    public void stop(boolean staticResourceCleanup) {
        synchronized (agentStatusLock) {
            if (this.agentStatus == AgentStatus.RUNNING) {
                changeStatus(AgentStatus.STOPPED);
            } else {
                logger.warn("Cannot stop agent. Current status = [{}]", this.agentStatus);
                return;
            }
        }
        logger.info("Stopping {} Agent.", ProductInfo.NAME);
        this.applicationContext.close();

        // for testcase
        if (staticResourceCleanup) {
            PLoggerFactory.unregister(this.binder);
            this.interceptorRegistryBinder.unbind();
        }
    }

}