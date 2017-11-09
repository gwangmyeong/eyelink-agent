package com.m2u.eyelink.context;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import com.m2u.eyelink.util.DelegateEnumeration;
import com.m2u.eyelink.util.EmptyEnumeration;

public enum Header {
    HTTP_TRACE_ID("ELAgent-TraceID"),
    HTTP_SPAN_ID("ELAgent-SpanID"),
    HTTP_PARENT_SPAN_ID("ELAgent-pSpanID"),
    HTTP_SAMPLED("ELAgent-Sampled"),
    HTTP_FLAGS("ELAgent-Flags"),
    HTTP_PARENT_APPLICATION_NAME("ELAgent-pAppName"),
    HTTP_PARENT_APPLICATION_TYPE("ELAgent-pAppType"),
    HTTP_HOST("ELAgent-Host");
    
    private String name;

    Header(String name) {
        this.name = name;
    }


    public String toString() {
        return name;
    }
    
    private static final Map<String, Header> NAME_SET = createMap();

    private static Map<String, Header> createMap() {
        Header[] headerList = values();
        Map<String, Header> map = new HashMap<String, Header>();
        for (Header header : headerList) {
            map.put(header.name, header);
        }
        return map;
    }

    public static Header getHeader(String name) {
        if (name == null) {
            return null;
        }
        if (!startWithELagentHeader(name)) {
            return null;
        }
        return NAME_SET.get(name);
    }



    public static boolean hasHeader(String name) {
        return getHeader(name) != null;
    }

    public static Enumeration getHeaders(String name) {
        if (name == null) {
            return null;
        }
        final Header header = getHeader(name);
        if (header == null) {
            return null;
        }
        // if elagent header
        return new EmptyEnumeration();
    }

    public static Enumeration filteredHeaderNames(final Enumeration enumeration) {
        return new DelegateEnumeration(enumeration, FILTER);
    }

    private static DelegateEnumeration.Filter FILTER = new DelegateEnumeration.Filter() {
        @Override
        public boolean filter(Object o) {
            if (o instanceof String) {
                return hasHeader((String )o);
            }
            return false;
        }
    };

    private static boolean startWithELagentHeader(String name) {
        return name.startsWith("ELAgent-");
    }
}
