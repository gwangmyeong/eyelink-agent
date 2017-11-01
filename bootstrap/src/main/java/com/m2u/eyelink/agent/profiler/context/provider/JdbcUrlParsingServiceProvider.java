package com.m2u.eyelink.agent.profiler.context.provider;

import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.m2u.eyelink.agent.plugin.jdbc.JdbcUrlParserV2;
import com.m2u.eyelink.agent.profiler.context.monitor.DefaultJdbcUrlParsingService;
import com.m2u.eyelink.agent.profiler.context.monitor.JdbcUrlParsingService;
import com.m2u.eyelink.agent.profiler.plugin.PluginContextLoadResult;

public class JdbcUrlParsingServiceProvider implements Provider<JdbcUrlParsingService> {

    private final Provider<PluginContextLoadResult> pluginContextLoadResultProvider;

    @Inject
    public JdbcUrlParsingServiceProvider(Provider<PluginContextLoadResult> pluginContextLoadResultProvider) {
        if (pluginContextLoadResultProvider == null) {
            throw new NullPointerException("pluginContextLoadResult must not be null");
        }
        this.pluginContextLoadResultProvider = pluginContextLoadResultProvider;
    }

    @Override
    public JdbcUrlParsingService get() {
        PluginContextLoadResult pluginContextLoadResult = this.pluginContextLoadResultProvider.get();
        List<JdbcUrlParserV2> jdbcUrlParserList = pluginContextLoadResult.getJdbcUrlParserList();

        return new DefaultJdbcUrlParsingService(jdbcUrlParserList);
    }

}
