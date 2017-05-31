package com.m2u.eyelink.agent.profiler.context.monitor;

import java.util.ArrayList;
import java.util.List;

import com.m2u.eyelink.context.monitor.DataSourceMonitorWrapper;
import com.m2u.eyelink.plugin.monitor.DataSourceMonitor;

public class DisabledDataSourceMonitorRegistryService implements DataSourceMonitorRegistryService {

    @Override
    public boolean register(DataSourceMonitor pluginMonitor) {
        return false;
    }

    @Override
    public boolean unregister(DataSourceMonitor pluginMonitor) {
        return false;
    }

    @Override
    public List<DataSourceMonitorWrapper> getPluginMonitorWrapperList() {
        return new ArrayList<DataSourceMonitorWrapper>();
    }

    @Override
    public int getRemainingIdNumber() {
        return 0;
    }

}
