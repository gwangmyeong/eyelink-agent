package com.m2u.eyelink.plugin.tomcat.interceptor;

import org.apache.catalina.connector.Connector;

import com.m2u.eyelink.agent.interceptor.AroundInterceptor;
import com.m2u.eyelink.context.ServerMetaDataHolder;
import com.m2u.eyelink.context.TraceContext;
import com.m2u.eyelink.logging.PLogger;
import com.m2u.eyelink.logging.PLoggerFactory;

public class ConnectorInitializeInterceptor implements AroundInterceptor {

    private PLogger logger = PLoggerFactory.getLogger(this.getClass());
    private final boolean isDebug = logger.isDebugEnabled();

    private TraceContext traceContext;
    
    public ConnectorInitializeInterceptor(TraceContext traceContext) {
        this.traceContext = traceContext;
    }

    @Override
    public void before(Object target, Object[] args) {

    }

    @Override
    public void after(Object target, Object[] args, Object result, Throwable throwable) {
        if (isDebug) {
            logger.afterInterceptor(target, args, result, throwable);
        }
        if (target instanceof Connector) {
            final Connector connector = (Connector) target;
            ServerMetaDataHolder holder = this.traceContext.getServerMetaDataHolder();
            holder.addConnector(connector.getProtocol(), connector.getPort());
            holder.notifyListeners();
        }
    }
}