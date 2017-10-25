package com.m2u.eyelink.collector.dao.elasticsearch.callstacks;

import com.m2u.eyelink.collector.bo.MethodTypeEnum;
import com.m2u.eyelink.common.trace.ServiceType;

public class Record {
    private final int tab;
    private final int id;
    private final int parentId;
    private final boolean method;

    private final String title;
    private String simpleClassName = "";
    private String fullApiDescription = "";

    private final String arguments;
    private final long begin;
    private final long elapsed;
    private final long gap;
    private final long executionMilliseconds;
    private final String agent;
    private final String applicationName;
    private final ServiceType serviceType;
    private final String destinationId;
    private final boolean excludeFromTimeline;

    private final String transactionId;
    private final long spanId;
    
    private boolean focused;
    private boolean hasChild;
    private boolean hasException;
    private MethodTypeEnum methodTypeEnum;
    private boolean isAuthorized;

    public Record(int tab, int id, int parentId, boolean method, String title, String arguments, long begin, long elapsed, long gap, String agent, String applicationName, ServiceType serviceType, String destinationId, boolean hasChild, boolean hasException, String transactionId, long spanId, long executionMilliseconds, MethodTypeEnum methodTypeEnum, boolean isAuthorized) {
        this.tab = tab;
        this.id = id;
        this.parentId = parentId;
        this.method = method;

        this.title = title;
        this.arguments = arguments;
        this.begin = begin;
        this.elapsed = elapsed;
        this.gap = gap;
        this.agent = agent;

        this.applicationName = applicationName;
        this.serviceType = serviceType;
        this.destinationId = destinationId;

        this.excludeFromTimeline = serviceType == null || serviceType.isInternalMethod();
        this.hasChild = hasChild;
        this.hasException = hasException;
        
        this.transactionId = transactionId;
        this.spanId = spanId;
        
        this.executionMilliseconds = executionMilliseconds;
        this.methodTypeEnum = methodTypeEnum;
        this.isAuthorized = isAuthorized;
    }

    public int getId() {
        return id;
    }

    public int getParentId() {
        return parentId;
    }

    public int getTab() {
        return tab;
    }
    public String getTabspace() {
        if(tab == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for(int i=0; i< tab; i++) {
            sb.append("&nbsp");
        }
        return sb.toString();
    }

    public boolean isMethod() {
        return method;
    }

    public String getTitle() {
        return title;
    }

    public String getArguments() {
        return arguments;
    }

    public long getBegin() {
        return begin;
    }

    public long getElapsed() {
        return elapsed;
    }

    public long getGap() {
        return gap;
    }

    public String getAgent() {
        return agent;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getApiType() {
        if (destinationId == null) {
            if (serviceType == null) {
                // no ServiceType when parameter
                return "";
            }
            return serviceType.getDesc();
        }
        if (serviceType.isIncludeDestinationId()) {
            return serviceType.getDesc() + "(" + destinationId + ")";
        } else {
            return serviceType.getDesc();
        }

    }

    public boolean isExcludeFromTimeline() {
        return excludeFromTimeline;
    }

    public String getSimpleClassName() {
        return simpleClassName;
    }

    public void setSimpleClassName(String simpleClassName) {
        this.simpleClassName = simpleClassName;
    }

    public String getFullApiDescription() {
        return fullApiDescription;
    }

    public void setFullApiDescription(String fullApiDescription) {
        this.fullApiDescription = fullApiDescription;
    }

    public boolean isFocused() {
        return focused;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    public boolean getHasChild() {
        return hasChild;
    }
    
    public boolean getHasException() {
        return hasException;
    }
    
    public String getTransactionId() {
        return transactionId;
    }

    public long getSpanId() {
        return spanId;
    }

    public long getExecutionMilliseconds() {
        return executionMilliseconds;
    }
    
    public MethodTypeEnum getMethodTypeEnum() {
        return methodTypeEnum;
    }
    
    public boolean isAuthorized() {
        return this.isAuthorized;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{tab=");
        builder.append(tab);
        builder.append(", id=");
        builder.append(id);
        builder.append(", parentId=");
        builder.append(parentId);
        builder.append(", method=");
        builder.append(method);
        builder.append(", title=");
        builder.append(title);
        builder.append(", simpleClassName=");
        builder.append(simpleClassName);
        builder.append(", fullApiDescription=");
        builder.append(fullApiDescription);
        builder.append(", arguments=");
        builder.append(arguments);
        builder.append(", begin=");
        builder.append(begin);
        builder.append(", elapsed=");
        builder.append(elapsed);
        builder.append(", gap=");
        builder.append(gap);
        builder.append(", executionMilliseconds=");
        builder.append(executionMilliseconds);
        builder.append(", agent=");
        builder.append(agent);
        builder.append(", applicationName=");
        builder.append(applicationName);
        builder.append(", serviceType=");
        builder.append(serviceType);
        builder.append(", destinationId=");
        builder.append(destinationId);
        builder.append(", excludeFromTimeline=");
        builder.append(excludeFromTimeline);
        builder.append(", transactionId=");
        builder.append(transactionId);
        builder.append(", spanId=");
        builder.append(spanId);
        builder.append(", focused=");
        builder.append(focused);
        builder.append(", hasChild=");
        builder.append(hasChild);
        builder.append(", hasException=");
        builder.append(hasException);
        builder.append(", methodTypeEnum=");
        builder.append(methodTypeEnum);
        builder.append(", isAuthorized=");
        builder.append(isAuthorized);
        builder.append("}");
        return builder.toString();
    }
}
