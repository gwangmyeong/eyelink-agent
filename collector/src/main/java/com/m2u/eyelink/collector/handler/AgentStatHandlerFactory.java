package com.m2u.eyelink.collector.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchAdminTemplate;
import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchTables;
import com.m2u.eyelink.collector.common.elasticsearch.TableName;

@Repository
public class AgentStatHandlerFactory implements FactoryBean<Handler> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

//    @Autowired
//    @Qualifier("agentStatHandler")
//    private AgentStatHandler v1;

    @Autowired
    @Qualifier("agentStatHandlerV2")
    private AgentStatHandlerV2 v2;

    @Autowired
    private ElasticSearchAdminTemplate adminTemplate;

    @Value("#{eyelink_collector_properties['collector.stat.format.compatibility.version'] ?: 'v2'}")
    private String mode = "v2";

    @Override
    public Handler getObject() throws Exception {
        logger.info("AgentStatHandler Mode {}", mode);

        final TableName v1TableName = ElasticSearchTables.AGENT_STAT;
        final TableName v2TableName = ElasticSearchTables.AGENT_STAT_VER2;

        if (mode.equalsIgnoreCase("v1")) {
            if (this.adminTemplate.tableExists(v1TableName)) {
            		// FIXME bsh 
//                return v1;
            		return null;
            } else {
                logger.error("AgentStatHandler configured for v1, but {} table does not exist", v1TableName);
                throw new IllegalStateException(v1TableName + " table does not exist");
            }
        } else if (mode.equalsIgnoreCase("v2")) {
            if (this.adminTemplate.tableExists(v2TableName)) {
                return v2;
            } else {
                logger.error("AgentStatHandler configured for v2, but {} table does not exist", v2TableName);
                throw new IllegalStateException(v2TableName + " table does not exist");
            }
        } else if (mode.equalsIgnoreCase("dualWrite")) {
            boolean v1TableExists = this.adminTemplate.tableExists(v1TableName);
            boolean v2TableExists = this.adminTemplate.tableExists(v2TableName);
            if (v1TableExists && v2TableExists) {
            		// FIXME bsh
                return new DualAgentStatHandler(null, v2);
            } else {
                logger.error("AgentStatHandler configured for dualWrite, but {} and {} tables do not exist", v1TableName, v2TableName);
                throw new IllegalStateException(v1TableName + ", " + v2TableName + " tables do not exist");
            }
        } else {
            throw new IllegalStateException("Unknown AgentStatHandler configuration : " + mode);
        }
    }

    @Override
    public Class<?> getObjectType() {
        return Handler.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
