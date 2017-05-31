package com.m2u.eyelink.agent.profiler.context.monitor;

import com.m2u.eyelink.agent.plugin.monitor.DataSourceMonitorRegistry;
import com.m2u.eyelink.plugin.monitor.DataSourceMonitor;

public class DataSourceMonitorRegistryAdaptor implements DataSourceMonitorRegistry {

    private final DataSourceMonitorRegistryService delegate;

    public DataSourceMonitorRegistryAdaptor(DataSourceMonitorRegistryService delegate) {
        if (delegate == null) {
            throw new NullPointerException("delegate must not be null");
        }
        this.delegate = delegate;
    }


    @Override
    public boolean register(DataSourceMonitor dataSourceMonitor) {
        return delegate.register(dataSourceMonitor);
    }

    @Override
    public boolean unregister(DataSourceMonitor dataSourceMonitor) {
        return delegate.unregister(dataSourceMonitor);
    }
}
