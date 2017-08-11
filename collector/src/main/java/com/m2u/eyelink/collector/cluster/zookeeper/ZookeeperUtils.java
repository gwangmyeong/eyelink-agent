package com.m2u.eyelink.collector.cluster.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;

public final class ZookeeperUtils {

    // would be a good idea to move to commons-hbase (if implemented) in the future
    private ZookeeperUtils() {
    }

    public static boolean isConnectedEvent(WatchedEvent event) {
        KeeperState state = event.getState();
        EventType eventType = event.getType();

        return isConnectedEvent(state, eventType);
    }

    public static boolean isConnectedEvent(KeeperState state, EventType eventType) {
        if ((state == KeeperState.SyncConnected || state == KeeperState.NoSyncConnected) && eventType == EventType.None) {
            return true;
        } else {
            return false;
        }
    }


    public static boolean isDisconnectedEvent(WatchedEvent event) {
        KeeperState state = event.getState();
        EventType eventType = event.getType();

        return isDisconnectedEvent(state, eventType);
    }

    public static boolean isDisconnectedEvent(KeeperState state, EventType eventType) {
        if ((state == KeeperState.Disconnected || state == KeeperState.Expired) && eventType == eventType.None) {
            return true;
        } else {
            return false;
        }
    }

}
