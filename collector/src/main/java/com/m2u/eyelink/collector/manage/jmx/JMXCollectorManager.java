package com.m2u.eyelink.collector.manage.jmx;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.m2u.eyelink.collector.manage.CollectorManager;

public class JMXCollectorManager {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final EyeLInkMBeanServer elagentMBeanServer;

    @Autowired
    private JMXCollectorManagerList jmxCollectorManagerList;
    
    public JMXCollectorManager() {
        this.elagentMBeanServer = new EyeLInkMBeanServer();
    }

    @PostConstruct
    public void setUp() {
        logger.info("ELAgentCollectorManager initialization started.");
        
        for (CollectorManager collectorManager : jmxCollectorManagerList.getSupportList()) {
            try {
                elagentMBeanServer.registerMBean(collectorManager);
            } catch (Exception e) {
                logger.warn("Failed to register {} MBean.", collectorManager, e);
            }
        }

        logger.info("ELAgentCollectorManager initialization completed.");
    }

    @PreDestroy
    public void tearDown() {
        logger.info("ELAgentCollectorManager finalization started.");

        for (CollectorManager collectorManager : jmxCollectorManagerList.getSupportList()) {
            try {
                elagentMBeanServer.unregisterMBean(collectorManager);
            } catch (Exception e) {
                logger.warn("Failed to unregister {} MBean.", collectorManager, e);
            }
        }

        logger.info("ELAgentCollectorManager finalization completed.");
    }
    
    public CollectorManager getMBean(String name) {
        return elagentMBeanServer.getELAgentMBean(name);
    }
    

}
