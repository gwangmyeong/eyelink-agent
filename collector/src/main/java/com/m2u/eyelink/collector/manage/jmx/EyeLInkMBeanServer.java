package com.m2u.eyelink.collector.manage.jmx;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.collector.manage.CollectorManager;

public final class EyeLInkMBeanServer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private final MBeanServer mBeanServer;
    private final Map<String, CollectorManager> eyelinkMBeanHolder;

    EyeLInkMBeanServer() {
        this.mBeanServer = ManagementFactory.getPlatformMBeanServer();
        this.eyelinkMBeanHolder = new HashMap<>();
    }

    public void registerMBean(CollectorManager pinpointMBean) {
        if (pinpointMBean == null) {
            return;
        }
        
        registerMBean(pinpointMBean.getName(), pinpointMBean);
    }
    
    public void registerMBean(String name, CollectorManager pinpointMBean) {
        logger.info("registerMBean {}", name);

        if (isRegistered(pinpointMBean)) {
            return;
        }

        try {
            ObjectName mBeanObjectName = createMBeanObjectName(name);
            mBeanServer.registerMBean(pinpointMBean, mBeanObjectName);
            eyelinkMBeanHolder.put(name, pinpointMBean);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void unregisterMBean(CollectorManager pinpointMBean) {
        if (pinpointMBean == null) {
            return;
        }

        unregisterMBean(pinpointMBean.getName());
    }

    public void unregisterMBean(String name) {
        logger.info("unregisterMBean {}", name);

        if (!isRegistered(name)) {
            return;
        }

        try {
            ObjectName mBeanObjectName = createMBeanObjectName(name);
            mBeanServer.unregisterMBean(mBeanObjectName);
            eyelinkMBeanHolder.remove(name);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public boolean isRegistered(CollectorManager pinpointMBean) {
        if (pinpointMBean == null) {
            return false;
        }

        return isRegistered(pinpointMBean.getName());
    }

    public boolean isRegistered(String name) {
        ObjectName objectMBeanName;
        try {
            objectMBeanName = createMBeanObjectName(name);
            return mBeanServer.isRegistered(objectMBeanName);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    public CollectorManager getELAgentMBean(String name) {
        return eyelinkMBeanHolder.get(name);
    }

    private static ObjectName createMBeanObjectName(String name) throws MalformedObjectNameException {
        String mBeanObjectName = "com.m2u.eyelink.collector.mbean:type=" + name;
        ObjectName objectName = new ObjectName(mBeanObjectName);
        return objectName;
    }

}
