package com.m2u.eyelink.plugin.tomcat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.m2u.eyelink.agent.profiler.logging.Slf4jLoggerBinder;
import com.m2u.eyelink.config.DefaultProfilerConfig;
import com.m2u.eyelink.config.ProfilerConfig;
import com.m2u.eyelink.context.DefaultMethodDescriptor;
import com.m2u.eyelink.context.DefaultTraceId;
import com.m2u.eyelink.context.Header;
import com.m2u.eyelink.context.MethodDescriptor;
import com.m2u.eyelink.context.TraceContext;
import com.m2u.eyelink.context.TraceId;
import com.m2u.eyelink.logging.PLoggerFactory;
import com.m2u.eyelink.plugin.tomcat.interceptor.StandardHostValveInvokeInterceptor;

public class InvokeMethodInterceptorTest {
    
    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;

    private final MethodDescriptor descriptor = new DefaultMethodDescriptor("org.apache.catalina.core.StandardHostValve", "invoke", new String[] {"org.apache.catalina.connector.Request", "org.apache.catalina.connector.Response"}, new String[] {"request", "response"});

    @BeforeClass
    public static void before() {
        PLoggerFactory.initialize(new Slf4jLoggerBinder());
    }
    
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    private TraceContext spyTraceContext() {
        ProfilerConfig profilerConfig = new DefaultProfilerConfig();
        TraceContext traceContext = MockTraceContextFactory.newTestTraceContext(profilerConfig);
        return spy(traceContext);
    }

    @Test
    public void testHeaderNOTExists() {

        when(request.getRequestURI()).thenReturn("/hellotest.nhn");
        when(request.getRemoteAddr()).thenReturn("10.0.0.1");
        when(request.getHeader(Header.HTTP_TRACE_ID.toString())).thenReturn(null);
        when(request.getHeader(Header.HTTP_PARENT_SPAN_ID.toString())).thenReturn(null);
        when(request.getHeader(Header.HTTP_SPAN_ID.toString())).thenReturn(null);
        when(request.getHeader(Header.HTTP_SAMPLED.toString())).thenReturn(null);
        when(request.getHeader(Header.HTTP_FLAGS.toString())).thenReturn(null);
        Enumeration<?> enumeration = mock(Enumeration.class);
        // FIXME BSH enumeration -> (Enumeration<String>) enumeration
        when(request.getParameterNames()).thenReturn((Enumeration<String>) enumeration);

        TraceContext traceContext = spyTraceContext();
        StandardHostValveInvokeInterceptor interceptor = new StandardHostValveInvokeInterceptor(traceContext, descriptor);

        interceptor.before("target", new Object[]{request, response});
        interceptor.after("target", new Object[]{request, response}, new Object(), null);

        verify(traceContext, times(1)).newTraceObject();

        interceptor.before("target", new Object[]{request, response});
        interceptor.after("target", new Object[]{request, response}, new Object(), null);

        verify(traceContext, times(2)).newTraceObject();
    }

    @Test
    public void testInvalidHeaderExists() {

        when(request.getRequestURI()).thenReturn("/hellotest.nhn");
        when(request.getRemoteAddr()).thenReturn("10.0.0.1");
        when(request.getHeader(Header.HTTP_TRACE_ID.toString())).thenReturn("TRACEID");
        when(request.getHeader(Header.HTTP_PARENT_SPAN_ID.toString())).thenReturn("PARENTSPANID");
        when(request.getHeader(Header.HTTP_SPAN_ID.toString())).thenReturn("SPANID");
        when(request.getHeader(Header.HTTP_SAMPLED.toString())).thenReturn("false");
        when(request.getHeader(Header.HTTP_FLAGS.toString())).thenReturn("0");
        Enumeration<?> enumeration = mock(Enumeration.class);
        // FIXME BSH enumeration -> (Enumeration<String>) enumeration
        when(request.getParameterNames()).thenReturn((Enumeration<String>) enumeration);

        TraceContext traceContext = spyTraceContext();
        StandardHostValveInvokeInterceptor interceptor = new StandardHostValveInvokeInterceptor(traceContext, descriptor);
        interceptor.before("target",  new Object[]{request, response});
        interceptor.after("target", new Object[]{request, response}, new Object(), null);

        // FIXME BSH enumeration -> (Enumeration<String>) enumeration
//        verify(traceContext, never()).newTraceObject();
//        verify(traceContext, never()).disableSampling();
//        verify(traceContext, never()).continueTraceObject(any(TraceId.class));


        interceptor.before("target", new Object[]{request, response});
        interceptor.after("target", new Object[]{request, response}, new Object(), null);

        // FIXME BSH enumeration -> (Enumeration<String>) enumeration
//        verify(traceContext, never()).newTraceObject();
//        verify(traceContext, never()).disableSampling();
//        verify(traceContext, never()).continueTraceObject(any(TraceId.class));
    }

    @Test
    public void testValidHeaderExists() {

        when(request.getRequestURI()).thenReturn("/hellotest.nhn");
        when(request.getRemoteAddr()).thenReturn("10.0.0.1");

        TraceId  traceId = new DefaultTraceId("agentTest", System.currentTimeMillis(), 1);
        when(request.getHeader(Header.HTTP_TRACE_ID.toString())).thenReturn(traceId.getTransactionId());
        when(request.getHeader(Header.HTTP_PARENT_SPAN_ID.toString())).thenReturn("PARENTSPANID");
        when(request.getHeader(Header.HTTP_SPAN_ID.toString())).thenReturn("SPANID");
        when(request.getHeader(Header.HTTP_SAMPLED.toString())).thenReturn("false");
        when(request.getHeader(Header.HTTP_FLAGS.toString())).thenReturn("0");
        Enumeration<?> enumeration = mock(Enumeration.class);
        // FIXME BSH enumeration -> (Enumeration<String>) enumeration
        when(request.getParameterNames()).thenReturn((Enumeration<String>) enumeration);

        TraceContext traceContext = spyTraceContext();
        StandardHostValveInvokeInterceptor interceptor = new StandardHostValveInvokeInterceptor(traceContext, descriptor);

        interceptor.before("target", new Object[]{request, response});
        interceptor.after("target", new Object[]{request, response}, new Object(), null);

//        verify(traceContext, times(1)).continueTraceObject(any(TraceId.class));

        interceptor.before("target", new Object[]{request, response});
        interceptor.after("target", new Object[]{request, response}, new Object(), null);

//        verify(traceContext, times(2)).continueTraceObject(any(TraceId.class));
    }
}
