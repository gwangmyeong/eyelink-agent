package com.m2u.eyelink.plugin.tomcat.interceptor.aspect;

import java.util.Enumeration;

import com.m2u.eyelink.agent.instrument.aspect.Aspect;
import com.m2u.eyelink.agent.instrument.aspect.JointPoint;
import com.m2u.eyelink.agent.instrument.aspect.PointCut;
import com.m2u.eyelink.context.Header;

@Aspect
public abstract class RequestFacadeAspect {

    @PointCut
    public String getHeader(String name) {
        if (Header.hasHeader(name)) {
            return null;
        }
        return __getHeader(name);
    }

    @JointPoint
    abstract String __getHeader(String name);


    @PointCut
    public Enumeration getHeaders(String name) {
        final Enumeration headers = Header.getHeaders(name);
        if (headers != null) {
            return headers;
        }
        return __getHeaders(name);
    }

    @JointPoint
    abstract Enumeration __getHeaders(String name);


    @PointCut
    public Enumeration getHeaderNames() {
        final Enumeration enumeration = __getHeaderNames();
        return Header.filteredHeaderNames(enumeration);
    }

    @JointPoint
    abstract Enumeration __getHeaderNames();

}
