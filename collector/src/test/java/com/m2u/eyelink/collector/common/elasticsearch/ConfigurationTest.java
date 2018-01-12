package com.m2u.eyelink.collector.common.elasticsearch;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Properties;

import org.junit.Test;

import com.m2u.eyelink.util.PropertyUtils;

public class ConfigurationTest {

	@Test
	public void getSet() throws IOException {
        Properties properties = PropertyUtils.loadPropertyFromClassPath("elasticsearch.properties");

        Configuration cfg = ElasticSearchConfiguration.create();
        cfg.set("elasticsearch.cluster.name", properties.getProperty("elasticsearch.cluster.name"));
        int i = 1;
        while(properties.getProperty("elasticsearch.host.ip."+i) != null) {
	        	cfg.set("elasticsearch.host.ip."+i, properties.getProperty("elasticsearch.host.ip."+i));
	        	cfg.set("elasticsearch.host.port."+i, properties.getProperty("elasticsearch.host.port."+i));
	        	i++;
        }
        System.out.println(cfg.toString());
        assertEquals(cfg.get("elasticsearch.cluster.name"), "eyelink-cluster-standalone");
	}
}
