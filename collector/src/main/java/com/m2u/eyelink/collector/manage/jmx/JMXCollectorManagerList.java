package com.m2u.eyelink.collector.manage.jmx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.m2u.eyelink.collector.manage.ClusterManager;
import com.m2u.eyelink.collector.manage.CollectorManager;
import com.m2u.eyelink.collector.manage.ElasticSearchManager;
import com.m2u.eyelink.collector.manage.HandlerManager;
import com.m2u.eyelink.rpc.util.ListUtils;

public class JMXCollectorManagerList {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("#{eyelink_collector_properties['collector.admin.api.jmx.active'] ?: false}")
    private boolean isActive;

    @Autowired
    private HandlerManager handlerManager;

    @Autowired
    private ClusterManager clusterManager;

    @Autowired
    private ElasticSearchManager elasticSearchManager;

    public List<CollectorManager> getSupportList() {
        if (!isActive) {
            logger.warn("not activating jmx api for admin.");
            return Collections.emptyList();
        }
        
        List<CollectorManager> supportManagerList = new ArrayList<>();

        ListUtils.addIfValueNotNull(supportManagerList, handlerManager);
        ListUtils.addIfValueNotNull(supportManagerList, clusterManager);
        ListUtils.addIfValueNotNull(supportManagerList, elasticSearchManager);

        return supportManagerList;
    }
    
}
