package com.m2u.eyelink.collector.cluster.connection;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.rpc.ELAgentSocket;
import com.m2u.eyelink.util.AssertUtils;
import com.m2u.eyelink.util.ClassUtils;

public class CollectorClusterConnectionManager {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String clusterId;
    private final CollectorClusterConnectionRepository socketRepository;

    private final CollectorClusterConnector clusterConnector;
    private final CollectorClusterAcceptor clusterAcceptor;

    public CollectorClusterConnectionManager(String clusterId, CollectorClusterConnectionRepository socketRepository, CollectorClusterConnector client) {
        this(clusterId, socketRepository, client, null);
    }

    public CollectorClusterConnectionManager(String clusterId, CollectorClusterConnectionRepository socketRepository, CollectorClusterConnector client, CollectorClusterAcceptor acceptor) {
        AssertUtils.assertNotNull(client, "clusterConnector may not be null.");

        this.clusterId = clusterId;
        this.socketRepository = socketRepository;
        this.clusterConnector = client;
        this.clusterAcceptor = acceptor;
    }

    public void start() {
        logger.info("{} initialization started.", ClassUtils.simpleClassName(this));

        if (clusterConnector != null) {
            clusterConnector.start();
        }

        if (clusterAcceptor != null) {
            clusterAcceptor.start();
        }

        logger.info("{} initialization completed.", ClassUtils.simpleClassName(this));
    }

    public void stop() {
        logger.info("{} destroying started.", ClassUtils.simpleClassName(this));

        for (ELAgentSocket socket : socketRepository.getClusterSocketList()) {
            if (socket != null) {
                socket.close();
            }
        }

        if (clusterConnector != null) {
            clusterConnector.stop();
        }

        if (clusterAcceptor != null) {
            clusterAcceptor.stop();
        }

        logger.info("{} destroying completed.", ClassUtils.simpleClassName(this));
    }

    public void connectPointIfAbsent(InetSocketAddress address) {
        logger.info("localhost -> {} connect started.", address);

        if (socketRepository.containsKey(address)) {
            logger.info("localhost -> {} already connected.", address);
            return;
        }

        socketRepository.putIfAbsent(address, clusterConnector.connect(address));

        logger.info("localhost -> {} connect completed.", address);
    }

    public void disconnectPoint(SocketAddress address) {
        logger.info("localhost -> {} disconnect started.", address);

        ELAgentSocket socket = socketRepository.remove(address);
        if (socket != null) {
            socket.close();
            logger.info("localhost -> {} disconnect completed.", address);
        } else {
            logger.info("localhost -> {} already disconnected.", address);
        }
    }

    public List<SocketAddress> getConnectedAddressList() {
        return socketRepository.getAddressList();
    }

}
