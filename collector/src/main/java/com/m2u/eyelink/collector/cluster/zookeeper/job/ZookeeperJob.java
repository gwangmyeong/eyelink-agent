package com.m2u.eyelink.collector.cluster.zookeeper.job;

import org.apache.commons.lang3.StringUtils;

public class ZookeeperJob {

    private final Type type;
    private final String key;
    public ZookeeperJob(Type type) {
        this(type, StringUtils.EMPTY);
    }

    public ZookeeperJob(Type type, String key) {
        this.type = type;
        this.key = key;
    }

    public Type getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public enum Type {
        ADD,
        REMOVE,
        CLEAR
    }

}
