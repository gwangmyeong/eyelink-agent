package com.m2u.eyelink.collector.dao.elasticsearch;

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
import com.m2u.eyelink.collector.dao.TraceDao;

@Repository
public class ElasticSearchTraceDaoFactory implements FactoryBean<TraceDao> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("elasticSearchTraceDao")
    private TraceDao v1;

    @Autowired
    @Qualifier("elasticSearchTraceDaoV2")
    private TraceDao v2;

    @Autowired
    private ElasticSearchAdminTemplate adminTemplate;

    @Value("#{pinpoint_collector_properties['collector.span.format.compatibility.version'] ?: 'v2'}")
    private String mode = "v2";

    @Override
    public TraceDao getObject() throws Exception {

        logger.info("TraceDao Compatibility {}", mode);

        final TableName v1TableName = ElasticSearchTables.TRACES;
        final TableName v2TableName = ElasticSearchTables.TRACE_V2;

        if (mode.equalsIgnoreCase("v1")) {
            if (this.adminTemplate.tableExists(v1TableName)) {
                return v1;
            } else {
                logger.error("TraceDao configured for v1, but {} table does not exist", v1TableName);
                throw new IllegalStateException(v1TableName + " table does not exist");
            }
        }
        else if (mode.equalsIgnoreCase("v2")) {
            if (this.adminTemplate.tableExists(v2TableName)) {
                return v2;
            } else {
                logger.error("TraceDao configured for v2, but {} table does not exist", v2TableName);
                throw new IllegalStateException(v2TableName + " table does not exist");
            }
        }
        else if(mode.equalsIgnoreCase("dualWrite")) {
            boolean v1TableExists = this.adminTemplate.tableExists(v1TableName);
            boolean v2TableExists = this.adminTemplate.tableExists(v2TableName);
            if (v1TableExists && v2TableExists) {
                return new DualWriteElasticSearchTraceDao(v1, v2);
            } else {
                logger.error("TraceDao configured for dualWrite, but {} and {} tables do not exist", v1TableName, v2TableName);
                throw new IllegalStateException(v1TableName + ", " + v2TableName + " tables do not exist");
            }
        }
        else {
            throw new IllegalStateException("Unknown TraceDao configuration : " + mode);
        }
    }

    @Override
    public Class<?> getObjectType() {
        return TraceDao.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
