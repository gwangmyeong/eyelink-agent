package com.m2u.eyelink.collector.util;

public interface ApiDescription {
    void setClassName(String className);

    String getClassName();

    String getSimpleClassName();

    String getPackageNameName();

    void setMethodName(String methodName);

    String getMethodName();

    void setSimpleParameter(String[] simpleParameter);

    String[] getSimpleParameter();

    void setLine(int line);

    String getSimpleMethodDescription();

    String concateLine(String[] stringList, String separator);
}
