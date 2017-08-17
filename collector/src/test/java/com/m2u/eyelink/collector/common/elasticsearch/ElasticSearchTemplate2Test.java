package com.m2u.eyelink.collector.common.elasticsearch;

import java.io.IOException;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.m2u.eyelink.util.PropertyUtils;

public class ElasticSearchTemplate2Test {
    private static ElasticSearchTemplate2 elasticSearchTemplate2;

    @BeforeClass
    public static void beforeClass() throws IOException {
        Properties properties = PropertyUtils.loadPropertyFromClassPath("elasticsearch.properties");

        Configuration cfg = ElasticSearchConfiguration.create();
        cfg.set("hbase.zookeeper.quorum", properties.getProperty("hbase.client.host"));
        cfg.set("hbase.zookeeper.property.clientPort", properties.getProperty("hbase.client.port"));
        
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
    public void notExist() throws Exception {
        try {
            elasticSearchTemplate2.put(TableName.valueOf("NOT_EXIST"), new byte[] {0, 0, 0}, "familyName".getBytes(), "columnName".getBytes(), new byte[]{0, 0, 0});
            Assert.fail("exceptions");
        } catch (ElasticSearchSystemException e) {
//            RetriesExhaustedWithDetailsException exception = (RetriesExhaustedWithDetailsException)(e.getCause());
//            if (!(exception.getCause(0) instanceof TableNotFoundException)) {
                Assert.fail("unexpected exception :" + e.getCause()); 
//            }
        }

    }
}
