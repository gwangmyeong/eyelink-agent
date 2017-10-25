package com.m2u.eyelink.collector.cluster.connection;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.rpc.ClusterOption;
import com.m2u.eyelink.rpc.ELAgentSocket;
import com.m2u.eyelink.rpc.client.DefaultELAgentClientFactory;
import com.m2u.eyelink.rpc.client.ELAgentClientFactory;
import com.m2u.eyelink.rpc.util.ClientFactoryUtils;
import com.m2u.eyelink.sender.Role;
import com.m2u.eyelink.util.ClassUtils;

public class CollectorClusterConnector implements CollectorClusterConnectionProvider {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final CollectorClusterConnectionOption option;

    private ELAgentClientFactory clientFactory;
    public CollectorClusterConnector(CollectorClusterConnectionOption option) {
        this.option = option;
    }

    @Override
    public void start() {
        logger.info("{} initialization started.", ClassUtils.simpleClassName(this));

        ClusterOption clusterOption = new ClusterOption(true, option.getClusterId(), Role.ROUTER);

        this.clientFactory = new DefaultELAgentClientFactory();

        this.clientFactory.setTimeoutMillis(1000 * 5);
        this.clientFactory.setMessageListener(option.getRouteMessageHandler());
        this.clientFactory.setServerStreamChannelMessageListener(option.getRouteStreamMessageHandler());
        this.clientFactory.setClusterOption(clusterOption);

        Map<String, Object> properties = new HashMap<>();
        properties.put("id", option.getClusterId());
        clientFactory.setProperties(properties);

        logger.info("{} initialization completed.", ClassUtils.simpleClassName(this));
    }

    @Override
    public void stop() {
        logger.info("{} destroying started.", ClassUtils.simpleClassName(this));

        if (clientFactory != null) {
            clientFactory.release();
        }

        logger.info("{} destroying completed.", ClassUtils.simpleClassName(this));
    }

    ELAgentSocket connect(InetSocketAddress address) {
        if (clientFactory == null) {
            throw new IllegalStateException("not started.");
        }

        ELAgentSocket socket = ClientFactoryUtils.createELAgentClient(address, clientFactory);
        return socket;
    }

}
