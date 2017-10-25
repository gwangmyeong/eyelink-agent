package com.m2u.eyelink.collector.common.elasticsearch;

import java.util.Enumeration;
import java.util.Properties;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

public class ElasticSearchConfigurationFactoryBean implements InitializingBean, FactoryBean<Configuration> {

    private Configuration configuration;
    private Configuration esConfig;
    private Properties properties;

    /**
     * Sets the Hadoop configuration to use.
     *
     * @param configuration The configuration to set.
     */
    public void setConfiguration(Configuration configuration) {
        this.esConfig = configuration;
    }

    /**
     * Sets the configuration properties.
     *
     * @param properties The properties to set.
     */
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void afterPropertiesSet() {
        configuration = (esConfig != null ? ElasticSearchConfiguration.create(esConfig) : ElasticSearchConfiguration.create());
        addProperties(configuration, properties);
    }
    
    /**
     * Adds the specified properties to the given {@link Configuration} object.  
     * 
     * @param configuration configuration to manipulate. Should not be null.
     * @param properties properties to add to the configuration. May be null.
     */
    private void addProperties(Configuration configuration, Properties properties) {
        Assert.notNull(configuration, "A non-null configuration is required");
        if (properties != null) {
            Enumeration<?> props = properties.propertyNames();
            while (props.hasMoreElements()) {
                String key = props.nextElement().toString();
                configuration.set(key, properties.getProperty(key));
            }
        }
    }

    public Configuration getObject() {
        return configuration;
    }

    public Class<? extends Configuration> getObjectType() {
        return (configuration != null ? configuration.getClass() : Configuration.class);
    }

    public boolean isSingleton() {
        return true;
    }
}