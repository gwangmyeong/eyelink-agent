package com.m2u.eyelink.collector.cluster.zookeeper;

import java.io.IOException;
import java.util.List;

import com.m2u.eyelink.collector.cluster.zookeeper.exception.ELAgentZookeeperException;

public interface ZookeeperClient {

    void connect() throws IOException;

    void reconnectWhenSessionExpired();

    void createPath(String path) throws ELAgentZookeeperException, InterruptedException;

    void createPath(String path, boolean createEndNode) throws ELAgentZookeeperException, InterruptedException;

    String createNode(String zNodePath, byte[] data) throws ELAgentZookeeperException, InterruptedException;

    byte[] getData(String path) throws ELAgentZookeeperException, InterruptedException;

    void setData(String path, byte[] data) throws ELAgentZookeeperException, InterruptedException;

    void delete(String path) throws ELAgentZookeeperException, InterruptedException;

    boolean exists(String path) throws ELAgentZookeeperException, InterruptedException;

    boolean isConnected();

    List<String> getChildrenNode(String path, boolean watch) throws ELAgentZookeeperException, InterruptedException;

    void close();

 }
