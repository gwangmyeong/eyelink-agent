package com.m2u.eyelink.agent.profiler.context.module;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.m2u.eyelink.agent.AgentOption;
import com.m2u.eyelink.agent.instrument.DynamicTransformTrigger;
import com.m2u.eyelink.agent.profiler.AgentInfoSender;
import com.m2u.eyelink.agent.profiler.ClassFileTransformerDispatcher;
import com.m2u.eyelink.agent.profiler.instrument.ASMBytecodeDumpService;
import com.m2u.eyelink.agent.profiler.instrument.BytecodeDumpTransformer;
import com.m2u.eyelink.agent.profiler.instrument.InstrumentEngine;
import com.m2u.eyelink.agent.profiler.interceptor.registry.InterceptorRegistryBinder;
import com.m2u.eyelink.agent.profiler.monitor.AgentStatMonitor;
import com.m2u.eyelink.common.service.ServiceTypeRegistryService;
import com.m2u.eyelink.config.ProfilerConfig;
import com.m2u.eyelink.context.AgentInformation;
import com.m2u.eyelink.context.TraceContext;
import com.m2u.eyelink.rpc.client.ELAgentClient;
import com.m2u.eyelink.rpc.client.ELAgentClientFactory;
import com.m2u.eyelink.sender.DataSender;
import com.m2u.eyelink.sender.EnhancedDataSender;

public class DefaultApplicationContext implements ApplicationContext {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ProfilerConfig profilerConfig;

    private final AgentInfoSender agentInfoSender;
    private final AgentStatMonitor agentStatMonitor;

    private final TraceContext traceContext;

    private final ELAgentClientFactory clientFactory;
    private final ELAgentClient client;
    private final EnhancedDataSender tcpDataSender;

    private final DataSender statDataSender;
    private final DataSender spanDataSender;

    private final AgentInformation agentInformation;
    private final AgentOption agentOption;

    private final ServiceTypeRegistryService serviceTypeRegistryService;

    private final ClassFileTransformerDispatcher classFileDispatcher;

    private final Instrumentation instrumentation;
    private final InstrumentEngine instrumentEngine;
    private final DynamicTransformTrigger dynamicTransformTrigger;

    private final Injector injector;


    public DefaultApplicationContext(AgentOption agentOption, final InterceptorRegistryBinder interceptorRegistryBinder) {
        if (agentOption == null) {
            throw new NullPointerException("agentOption must not be null");
        }
        if (interceptorRegistryBinder == null) {
            throw new NullPointerException("interceptorRegistryBinder must not be null");
        }

        this.agentOption = agentOption;
        this.profilerConfig = agentOption.getProfilerConfig();
        this.instrumentation = agentOption.getInstrumentation();
        this.serviceTypeRegistryService = agentOption.getServiceTypeRegistryService();

        if (logger.isInfoEnabled()) {
            logger.info("DefaultAgent classLoader:{}", this.getClass().getClassLoader());
        }

        final Module applicationContextModule = newApplicationContextModule(agentOption, interceptorRegistryBinder);
        this.injector = Guice.createInjector(Stage.PRODUCTION, applicationContextModule);

        this.instrumentEngine = injector.getInstance(InstrumentEngine.class);

        this.classFileDispatcher = injector.getInstance(ClassFileTransformerDispatcher.class);
        this.dynamicTransformTrigger = injector.getInstance(DynamicTransformTrigger.class);
//        ClassFileTransformer classFileTransformer = injector.getInstance(ClassFileTransformer.class);
        ClassFileTransformer classFileTransformer = wrap(classFileDispatcher);
        instrumentation.addTransformer(classFileTransformer, true);

        this.spanDataSender = newUdpSpanDataSender();
        logger.info("spanDataSender:{}", spanDataSender);

        this.statDataSender = newUdpStatDataSender();
        logger.info("statDataSender:{}", statDataSender);

        this.clientFactory = injector.getInstance(ELAgentClientFactory.class);
        logger.info("clientFactory:{}", clientFactory);

        this.client = injector.getInstance(ELAgentClient.class);
        logger.info("client:{}", client);

        this.tcpDataSender = injector.getInstance(EnhancedDataSender.class);
        logger.info("tcpDataSender:{}", tcpDataSender);

        this.traceContext = injector.getInstance(TraceContext.class);

        this.agentInformation = injector.getInstance(AgentInformation.class);
        logger.info("agentInformation:{}", agentInformation);

        this.agentInfoSender = injector.getInstance(AgentInfoSender.class);
        this.agentStatMonitor = injector.getInstance(AgentStatMonitor.class);
    }

    public ClassFileTransformer wrap(ClassFileTransformerDispatcher classFileTransformerDispatcher) {

        final boolean enableBytecodeDump = profilerConfig.readBoolean(ASMBytecodeDumpService.ENABLE_BYTECODE_DUMP, ASMBytecodeDumpService.ENABLE_BYTECODE_DUMP_DEFAULT_VALUE);
        if (enableBytecodeDump) {
            logger.info("wrapBytecodeDumpTransformer");
            return BytecodeDumpTransformer.wrap(classFileTransformerDispatcher, profilerConfig);
        }
        return classFileTransformerDispatcher;
    }

    protected Module newApplicationContextModule(AgentOption agentOption, InterceptorRegistryBinder interceptorRegistryBinder) {
        return new ApplicationContextModule(agentOption, profilerConfig, serviceTypeRegistryService, interceptorRegistryBinder);
    }

    private DataSender newUdpStatDataSender() {

        Key<DataSender> statDataSenderKey = Key.get(DataSender.class, StatDataSender.class);
        return injector.getInstance(statDataSenderKey);
    }

    private DataSender newUdpSpanDataSender() {
        Key<DataSender> spanDataSenderKey = Key.get(DataSender.class, SpanDataSender.class);
        return injector.getInstance(spanDataSenderKey);
    }

    @Override
    public ProfilerConfig getProfilerConfig() {
        return profilerConfig;
    }

    public Injector getInjector() {
        return injector;
    }

    @Override
    public TraceContext getTraceContext() {
        return traceContext;
    }

    public DataSender getSpanDataSender() {
        return spanDataSender;
    }

    public InstrumentEngine getInstrumentEngine() {
        return instrumentEngine;
    }


    @Override
    public DynamicTransformTrigger getDynamicTransformTrigger() {
        return dynamicTransformTrigger;
    }


    @Override
    public ClassFileTransformerDispatcher getClassFileTransformerDispatcher() {
        return classFileDispatcher;
    }

    @Override
    public AgentInformation getAgentInformation() {
        return this.agentInformation;
    }



    @Override
    public void start() {
        this.agentInfoSender.start();
        this.agentStatMonitor.start();
    }

    @Override
    public void close() {
        this.agentInfoSender.stop();
        this.agentStatMonitor.stop();

        // Need to process stop
        this.spanDataSender.stop();
        this.statDataSender.stop();

        closeTcpDataSender();
    }

    private void closeTcpDataSender() {
        final EnhancedDataSender tcpDataSender = this.tcpDataSender;
        if (tcpDataSender != null) {
            tcpDataSender.stop();
        }
        final ELAgentClient client = this.client;
        if (client != null) {
            client.close();
        }
        final ELAgentClientFactory clientFactory = this.clientFactory;
        if (clientFactory != null) {
            clientFactory.release();
        }
    }

}
