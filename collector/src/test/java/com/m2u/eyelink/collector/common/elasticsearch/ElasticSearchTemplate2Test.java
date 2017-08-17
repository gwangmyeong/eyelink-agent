package com.m2u.eyelink.collector.common.elasticsearch;

import java.io.IOException;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.m2u.eyelink.collector.util.ElasticSearchUtils;
import com.m2u.eyelink.util.PropertyUtils;

public class ElasticSearchTemplate2Test {
    private static ElasticSearchTemplate2 elasticSearchTemplate2;

    @BeforeClass
    public static void beforeClass() throws IOException {
        Properties properties = PropertyUtils.loadPropertyFromClassPath("elasticsearch.properties");

        Configuration cfg = ElasticSearchConfiguration.create();
        cfg.set("elasticsearch.cluster.name", properties.getProperty("elasticsearch.cluster.name"));
        int i = 1;
        while(properties.getProperty("elasticsearch.host.ip."+i) != null) {
	        	cfg.set("elasticsearch.host.ip."+i, properties.getProperty("elasticsearch.host.ip."+i));
	        	cfg.set("elasticsearch.host.port."+i, properties.getProperty("elasticsearch.host.port."+i));
	        	i++;
        }
        
        elasticSearchTemplate2 = new ElasticSearchTemplate2();
        elasticSearchTemplate2.setConfiguration(cfg);
        elasticSearchTemplate2.setTableFactory(new PooledElasticSearchFactory(cfg));
        elasticSearchTemplate2.afterPropertiesSet();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        if (elasticSearchTemplate2 != null) {
            elasticSearchTemplate2.destroy();
        }
    }

    @Test
//    @Ignore
    public void insertJsonData() throws Exception {
        try {
	    		String json = "{" +
	    		        "\"user\":\"kimchy\"," +
	    		        "\"postDate\":\"2013-01-30\"," +
	    		        "\"message\":\"trying out Elasticsearch\"" +
	    		    "}";

            elasticSearchTemplate2.put(ElasticSearchUtils.generateIndexName("TEST"), ElasticSearchTables.TYPE_AGENTINFO, json);
        } catch (ElasticSearchSystemException e) {
//            RetriesExhaustedWithDetailsException exception = (RetriesExhaustedWithDetailsException)(e.getCause());
//            if (!(exception.getCause(0) instanceof TableNotFoundException)) {
                Assert.fail("unexpected exception :" + e.getCause()); 
//            }
        }

    }
}
