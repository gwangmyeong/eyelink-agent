package com.m2u.eyelink.util;

public interface SimpleProperty {
    void setProperty(String key, String value);

    String getProperty(String key);

    String getProperty(String key, String defaultValue);

}
