package com.m2u.eyelink.collector.cluster.zookeeper;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.collector.cluster.ClusterPointRepository;
import com.m2u.eyelink.collector.cluster.CommonStateContext;
import com.m2u.eyelink.collector.cluster.ELAgentServerClusterPoint;
import com.m2u.eyelink.context.HandshakePropertyType;
import com.m2u.eyelink.rpc.SocketStateCode;
import com.m2u.eyelink.rpc.server.ELAgentServer;
import com.m2u.eyelink.rpc.server.ServerStateChangeEventHandler;
import com.m2u.eyelink.rpc.util.MapUtils;

public class ZookeeperProfilerClusterManager implements ServerStateChangeEventHandler {

    private static final Charset charset = Charset.forName("UTF-8");

    private static final String PROFILER_SEPARATOR = "\r\n";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ZookeeperJobWorker worker;

    private final CommonStateContext workerState;

    private final ClusterPointRepository profileCluster;

    private final Object lock = new Object();

    // keep it simple - register on RUN, remove on FINISHED, skip otherwise
    // should only be instantiated when cluster is enabled.
    public ZookeeperProfilerClusterManager(ZookeeperClient client, String serverIdentifier, ClusterPointRepository profileCluster) {
        this.workerState = new CommonStateContext();
        this.profileCluster = profileCluster;

        this.worker = new ZookeeperJobWorker(client, serverIdentifier);
    }

    public void start() {
        switch (this.workerState.getCurrentState()) {
            case NEW:
                if (this.workerState.changeStateInitializing()) {
                    logger.info("start() started.");

                    worker.start();
                    workerState.changeStateStarted();

                    logger.info("start() completed.");

                    break;
                }
            case INITIALIZING:
                logger.info("start() failed. caused:already initializing.");
                break;
            case STARTED:
                logger.info("start() failed. caused:already started.");
                break;
            case DESTROYING:
                throw new IllegalStateException("Already destroying.");
            case STOPPED:
                throw new IllegalStateException("Already stopped.");
            case ILLEGAL_STATE:
                throw new IllegalStateException("Invalid State.");
        }
    }

    public void stop() {
        if (!(this.workerState.changeStateDestroying())) {
            logger.info("stop() failed. caused:unexpected state.");
            return;
        }

        logger.info("stop() started.");

        worker.stop();
        this.workerState.changeStateStopped();

        logger.info("stop() completed.");
    }

    @Override
    public void eventPerformed(ELAgentServer pinpointServer, SocketStateCode stateCode) {
        if (workerState.isStarted()) {
            logger.info("eventPerformed() started. (ELAgentServer={}, State={})", pinpointServer, stateCode);

            Map agentProperties = pinpointServer.getChannelProperties();

            // skip when applicationName and agentId is unknown
            if (skipAgent(agentProperties)) {
                return;
            }

            synchronized (lock) {
                if (SocketStateCode.RUN_DUPLEX == stateCode) {
                    profileCluster.addClusterPoint(new ELAgentServerClusterPoint(pinpointServer));
                    worker.addELAgentServer(pinpointServer);
                } else if (SocketStateCode.isClosed(stateCode)) {
                    profileCluster.removeClusterPoint(new ELAgentServerClusterPoint(pinpointServer));
                    worker.removeELAgentServer(pinpointServer);
                }
            }
        } else {
            logger.info("eventPerformed() failed. caused:unexpected state.");
        }
    }
    
    @Override
    public void exceptionCaught(ELAgentServer pinpointServer, SocketStateCode stateCode, Throwable e) {
        logger.warn("exceptionCaught(). (pinpointServer:{}, ELAgentServerStateCode:{}). caused:{}.", pinpointServer, stateCode, e.getMessage(), e);
    }

    public List<String> getClusterData() {
        byte[] contents = worker.getClusterData();
        if (contents == null) {
            return Collections.emptyList();
        }


        String clusterData = new String(contents, charset);
        String[] allClusterData = clusterData.split(PROFILER_SEPARATOR);

        List<String> result = new ArrayList<>(allClusterData.length);
        for (String eachClusterData : allClusterData) {
            if (!StringUtils.isBlank(eachClusterData)) {
                result.add(eachClusterData);
            }
        }

        return result;
    }

    public void initZookeeperClusterData() {
        worker.clear();

        synchronized (lock) {
            List clusterPointList = profileCluster.getClusterPointList();
            for (Object clusterPoint : clusterPointList) {
                if (clusterPoint instanceof ELAgentServerClusterPoint) {
                    ELAgentServer pinpointServer = ((ELAgentServerClusterPoint) clusterPoint).getELAgentServer();
                    if (SocketStateCode.isRunDuplex(pinpointServer.getCurrentStateCode())) {
                        worker.addELAgentServer(pinpointServer);
                    }
                }
            }
        }
    }

    private boolean skipAgent(Map<Object, Object> agentProperties) {
        String applicationName = MapUtils.getString(agentProperties, HandshakePropertyType.APPLICATION_NAME.getName());
        String agentId = MapUtils.getString(agentProperties, HandshakePropertyType.AGENT_ID.getName());

        if (StringUtils.isBlank(applicationName) || StringUtils.isBlank(agentId)) {
            return true;
        }

        return false;
    }

}
