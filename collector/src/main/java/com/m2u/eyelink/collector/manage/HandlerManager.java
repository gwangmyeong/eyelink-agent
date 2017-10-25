package com.m2u.eyelink.collector.manage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HandlerManager extends AbstractCollectorManager implements HandlerManagerMBean {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private volatile boolean enable = true;

    @Override
    public void enableAccess() {
        logger.warn("Enable access to manager.");
        this.enable = true;
    }

    @Override
    public void disableAccess() {
        logger.warn("Disable access to manager.");
        this.enable = false;
    }

    @Override
    public boolean isEnable() {
        return enable;
    }

}
