package com.m2u.eyelink.plugin.tomcat;

import java.security.ProtectionDomain;

import com.m2u.eyelink.agent.async.AsyncTraceIdAccessor;
import com.m2u.eyelink.agent.instrument.InstrumentClass;
import com.m2u.eyelink.agent.instrument.InstrumentException;
import com.m2u.eyelink.agent.instrument.InstrumentMethod;
import com.m2u.eyelink.agent.instrument.Instrumentor;
import com.m2u.eyelink.agent.instrument.MethodFilters;
import com.m2u.eyelink.agent.instrument.transformer.TransformCallback;
import com.m2u.eyelink.agent.instrument.transformer.TransformTemplate;
import com.m2u.eyelink.agent.instrument.transformer.TransformTemplateAware;
import com.m2u.eyelink.agent.plugin.ProfilerPlugin;
import com.m2u.eyelink.agent.plugin.ProfilerPluginSetupContext;
import com.m2u.eyelink.agent.resolver.ConditionProvider;
import com.m2u.eyelink.logging.PLogger;
import com.m2u.eyelink.logging.PLoggerFactory;

public class TomcatPlugin implements ProfilerPlugin, TransformTemplateAware {

    private final PLogger logger = PLoggerFactory.getLogger(this.getClass());

    private TransformTemplate transformTemplate;

    /*
     * (non-Javadoc)
     * 
     * @see com.m2u.eyelink.agent.profiler.plugin.ProfilerPlugin#setUp(com.m2u.eyelink.agent.profiler.plugin.ProfilerPluginSetupContext)
     */
    @Override
    public void setup(ProfilerPluginSetupContext context) {

        final TomcatConfig config = new TomcatConfig(context.getConfig());
        if (logger.isInfoEnabled()) {
            logger.info("TomcatPlugin config:{}", config);
        }
        if (!config.isTomcatEnable()) {
            logger.info("TomcatPlugin disabled");
            return;
        }

        TomcatDetector tomcatDetector = new TomcatDetector(config.getTomcatBootstrapMains());
        context.addApplicationTypeDetector(tomcatDetector);

        if (shouldAddTransformers(config)) {
            logger.info("Adding Tomcat transformers");
            addTransformers(config);
        } else {
            logger.info("Not adding Tomcat transfomers");
        }
    }

    private boolean shouldAddTransformers(TomcatConfig config) {
        // Transform if conditional check is disabled
        if (!config.isTomcatConditionalTransformEnable()) {
            return true;
        }
        // Only transform if it's a Tomcat application or SpringBoot application
        ConditionProvider conditionProvider = ConditionProvider.DEFAULT_CONDITION_PROVIDER;
        boolean isTomcatApplication = conditionProvider.checkMainClass(config.getTomcatBootstrapMains());
        boolean isSpringBootApplication = conditionProvider.checkMainClass(config.getSpringBootBootstrapMains());
        return isTomcatApplication || isSpringBootApplication;
}

    private void addTransformers(TomcatConfig config) {
        if (config.isTomcatHidePinpointHeader()) {
            addRequestFacadeEditor();
        }

        addRequestEditor();
        addStandardHostValveEditor();
        addStandardServiceEditor();
        addTomcatConnectorEditor();
        addWebappLoaderEditor();

        addAsyncContextImpl();
    }

    private void addRequestEditor() {
        transformTemplate.transform("org.apache.catalina.connector.Request", new TransformCallback() {

            @Override
            public byte[] doInTransform(Instrumentor instrumentor, ClassLoader classLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws InstrumentException {
                InstrumentClass target = instrumentor.getInstrumentClass(classLoader, className, classfileBuffer);
                target.addField(TomcatConstants.TRACE_ACCESSOR);
                target.addField(TomcatConstants.ASYNC_ACCESSOR);

                // clear request.
                InstrumentMethod recycleMethodEditorBuilder = target.getDeclaredMethod("recycle");
                if (recycleMethodEditorBuilder != null) {
                    recycleMethodEditorBuilder.addInterceptor("com.m2u.eyelink.plugin.tomcat.interceptor.RequestRecycleInterceptor");
                }

                // trace asynchronous process.
                InstrumentMethod startAsyncMethodEditor = target.getDeclaredMethod("startAsync", "javax.servlet.ServletRequest", "javax.servlet.ServletResponse");
                if (startAsyncMethodEditor != null) {
                    startAsyncMethodEditor.addInterceptor("com.m2u.eyelink.plugin.tomcat.interceptor.RequestStartAsyncInterceptor");
                }

                return target.toBytecode();
            }
        });
    }

    private void addRequestFacadeEditor() {
        transformTemplate.transform("org.apache.catalina.connector.RequestFacade", new TransformCallback() {

            @Override
            public byte[] doInTransform(Instrumentor instrumentor, ClassLoader classLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws InstrumentException {
                InstrumentClass target = instrumentor.getInstrumentClass(classLoader, className, classfileBuffer);
                if (target != null) {
                    target.weave("com.m2u.eyelink.plugin.tomcat.interceptor.aspect.RequestFacadeAspect");
                    return target.toBytecode();
                }

                return null;
            }
        });
    }

    private void addStandardHostValveEditor() {
        transformTemplate.transform("org.apache.catalina.core.StandardHostValve", new TransformCallback() {

            @Override
            public byte[] doInTransform(Instrumentor instrumentor, ClassLoader classLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws InstrumentException {
                InstrumentClass target = instrumentor.getInstrumentClass(classLoader, className, classfileBuffer);

                InstrumentMethod method = target.getDeclaredMethod("invoke", "org.apache.catalina.connector.Request", "org.apache.catalina.connector.Response");
                if (method != null) {
                    method.addInterceptor("com.m2u.eyelink.plugin.tomcat.interceptor.StandardHostValveInvokeInterceptor");
                }

                return target.toBytecode();
            }
        });
    }

    private void addStandardServiceEditor() {
        transformTemplate.transform("org.apache.catalina.core.StandardService", new TransformCallback() {

            @Override
            public byte[] doInTransform(Instrumentor instrumentor, ClassLoader classLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws InstrumentException {
                InstrumentClass target = instrumentor.getInstrumentClass(classLoader, className, classfileBuffer);

                // Tomcat 6
                InstrumentMethod startEditor = target.getDeclaredMethod("start");
                if (startEditor != null) {
                    startEditor.addInterceptor("com.m2u.eyelink.plugin.tomcat.interceptor.StandardServiceStartInterceptor");
                }

                // Tomcat 7
                InstrumentMethod startInternalEditor = target.getDeclaredMethod("startInternal");
                if (startInternalEditor != null) {
                    startInternalEditor.addInterceptor("com.m2u.eyelink.plugin.tomcat.interceptor.StandardServiceStartInterceptor");
                }

                return target.toBytecode();
            }
        });
    }

    private void addTomcatConnectorEditor() {
        transformTemplate.transform("org.apache.catalina.connector.Connector", new TransformCallback() {

            @Override
            public byte[] doInTransform(Instrumentor instrumentor, ClassLoader classLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws InstrumentException {
                InstrumentClass target = instrumentor.getInstrumentClass(classLoader, className, classfileBuffer);

                // Tomcat 6
                InstrumentMethod initializeEditor = target.getDeclaredMethod("initialize");
                if (initializeEditor != null) {
                    initializeEditor.addInterceptor("com.m2u.eyelink.plugin.tomcat.interceptor.ConnectorInitializeInterceptor");
                }

                // Tomcat 7
                InstrumentMethod initInternalEditor = target.getDeclaredMethod("initInternal");
                if (initInternalEditor != null) {
                    initInternalEditor.addInterceptor("com.m2u.eyelink.plugin.tomcat.interceptor.ConnectorInitializeInterceptor");
                }

                return target.toBytecode();
            }
        });
    }

    private void addWebappLoaderEditor() {
        transformTemplate.transform("org.apache.catalina.loader.WebappLoader", new TransformCallback() {

            @Override
            public byte[] doInTransform(Instrumentor instrumentor, ClassLoader classLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws InstrumentException {
                InstrumentClass target = instrumentor.getInstrumentClass(classLoader, className, classfileBuffer);

                InstrumentMethod startMethod = null;
                if (target.hasDeclaredMethod("start")) {
                    // Tomcat 6 - org.apache.catalina.loader.WebappLoader.start()
                    startMethod = target.getDeclaredMethod("start");
                } else if (target.hasDeclaredMethod("startInternal")) {
                    // Tomcat 7, 8 - org.apache.catalina.loader.WebappLoader.startInternal()
                    startMethod = target.getDeclaredMethod("startInternal");
                }

                if (startMethod != null) {
                    startMethod.addInterceptor("com.m2u.eyelink.plugin.tomcat.interceptor.WebappLoaderStartInterceptor");
                }

                return target.toBytecode();
            }
        });
    }

    private void addAsyncContextImpl() {
        transformTemplate.transform("org.apache.catalina.core.AsyncContextImpl", new TransformCallback() {

            @Override
            public byte[] doInTransform(Instrumentor instrumentor, ClassLoader classLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws InstrumentException {
                InstrumentClass target = instrumentor.getInstrumentClass(classLoader, className, classfileBuffer);
                target.addField(AsyncTraceIdAccessor.class.getName());
                for (InstrumentMethod method : target.getDeclaredMethods(MethodFilters.name("dispatch"))) {
                    method.addInterceptor("com.m2u.eyelink.plugin.tomcat.interceptor.AsyncContextImplDispatchMethodInterceptor");
                }

                return target.toBytecode();
            }
        });
    }

    @Override
    public void setTransformTemplate(TransformTemplate transformTemplate) {
        this.transformTemplate = transformTemplate;
    }
}
