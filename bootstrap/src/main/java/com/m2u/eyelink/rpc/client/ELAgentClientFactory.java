package com.m2u.eyelink.rpc.client;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.Map;

import org.jboss.netty.channel.ChannelFuture;

import com.m2u.eyelink.rpc.ClusterOption;
import com.m2u.eyelink.rpc.ELAgentSocketException;
import com.m2u.eyelink.rpc.MessageListener;
import com.m2u.eyelink.rpc.ServerStreamChannelMessageListener;
import com.m2u.eyelink.rpc.StateChangeEventListener;
import com.m2u.eyelink.sender.Role;

public interface ELAgentClientFactory {


    void setConnectTimeout(int connectTimeout);

    int getConnectTimeout();

    long getReconnectDelay();

    void setReconnectDelay(long reconnectDelay);

    long getPingDelay();

    void setPingDelay(long pingDelay);

    long getEnableWorkerPacketDelay();

    void setEnableWorkerPacketDelay(long enableWorkerPacketDelay);

    long getTimeoutMillis();

    void setTimeoutMillis(long timeoutMillis);


    ELAgentClient connect(String host, int port) throws ELAgentSocketException;

    ELAgentClient connect(InetSocketAddress connectAddress) throws ELAgentSocketException;

    ELAgentClient reconnect(String host, int port) throws ELAgentSocketException;


    ELAgentClient scheduledConnect(String host, int port);

    ELAgentClient scheduledConnect(InetSocketAddress connectAddress);


    ChannelFuture reconnect(final SocketAddress remoteAddress);


    void release();


    void setProperties(Map<String, Object> agentProperties);

    ClusterOption getClusterOption();

    void setClusterOption(String id, List<Role> roles);

    void setClusterOption(ClusterOption clusterOption);

    MessageListener getMessageListener();

    MessageListener getMessageListener(MessageListener defaultMessageListener);

    void setMessageListener(MessageListener messageListener);

    ServerStreamChannelMessageListener getServerStreamChannelMessageListener();

    ServerStreamChannelMessageListener getServerStreamChannelMessageListener(ServerStreamChannelMessageListener defaultStreamMessageListener);


    void setServerStreamChannelMessageListener(ServerStreamChannelMessageListener serverStreamChannelMessageListener);

    List<StateChangeEventListener> getStateChangeEventListeners();

    void addStateChangeEventListener(StateChangeEventListener stateChangeEventListener);

//    boolean isReleased();
//
//    int issueNewSocketId();

}
