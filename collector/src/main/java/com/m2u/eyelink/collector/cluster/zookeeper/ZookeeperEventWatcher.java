package com.m2u.eyelink.collector.cluster.zookeeper;

import org.apache.zookeeper.Watcher;

public interface ZookeeperEventWatcher extends Watcher {

    boolean isConnected();

}
