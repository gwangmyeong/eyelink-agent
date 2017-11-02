package com.m2u.eyelink.plugin.tomcat.interceptor;

import org.apache.catalina.util.ServerInfo;

import com.m2u.eyelink.agent.interceptor.AroundInterceptor;
import com.m2u.eyelink.agent.interceptor.AroundInterceptor;
import com.m2u.eyelink.context.ServerMetaDataHolder;
import com.m2u.eyelink.context.TraceContext;
import com.m2u.eyelink.logging.PLogger;
import com.m2u.eyelink.logging.PLoggerFactory;

public class StandardServiceStartInterceptor implements AroundInterceptor {

    private final PLogger logger = PLoggerFactory.getLogger(this.getClass());
    private final boolean isDebug = logger.isDebugEnabled();

    private final TraceContext traceContext;

    public StandardServiceStartInterceptor(TraceContext context) {
        this.traceContext = context;
    }

    @Override
    public void before(Object target, Object[] args) {
        // Do nothing
    }

    @Override
    public void after(Object target, Object[] args, Object result, Throwable throwable) {
        if (isDebug) {
            logger.afterInterceptor(target, args, result, throwable);
        }

        String serverInfo = ServerInfo.getServerInfo();
        ServerMetaDataHolder holder = this.traceContext.getServerMetaDataHolder();
        holder.setServerName(serverInfo);
        holder.notifyListeners();
    }
}
