package com.m2u.eyelink.collector.manage;

public abstract class AbstractCollectorManager implements CollectorManager {

    @Override
    public String getName() {
        return simpleClassName();
    }

    private String simpleClassName() {
        Class clazz = this.getClass();

        Package pkg = clazz.getPackage();
        if (pkg != null) {
            return clazz.getName().substring(pkg.getName().length() + 1);
        } else {
            return clazz.getName();
        }
    }

}