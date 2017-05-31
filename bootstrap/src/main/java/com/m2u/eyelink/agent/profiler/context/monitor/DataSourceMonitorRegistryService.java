package com.m2u.eyelink.agent.profiler.context.monitor;

import java.util.List;

import com.m2u.eyelink.context.monitor.DataSourceMonitorWrapper;
import com.m2u.eyelink.plugin.monitor.DataSourceMonitor;

public interface DataSourceMonitorRegistryService {

    boolean register(DataSourceMonitor dataSourceMonitor);

    boolean unregister(DataSourceMonitor dataSourceMonitor);

    List<DataSourceMonitorWrapper> getPluginMonitorWrapperList();

    int getRemainingIdNumber();
}
