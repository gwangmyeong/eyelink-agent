package com.m2u.eyelink.agent.plugin.monitor;

import com.m2u.eyelink.plugin.monitor.DataSourceMonitor;

public interface DataSourceMonitorRegistry {

    boolean register(DataSourceMonitor dataSourceMonitor);

    boolean unregister(DataSourceMonitor dataSourceMonitor);


}
