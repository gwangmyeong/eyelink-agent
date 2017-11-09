package com.m2u.eyelink.agent.plugin;

import com.m2u.eyelink.agent.plugin.jdbc.JdbcUrlParserV2;
import com.m2u.eyelink.config.ProfilerConfig;

public interface ProfilerPluginSetupContext {
    /**
     * Get the {@link ProfilerConfig}
     * 
     * @return {@link ProfilerConfig}
     */
    ProfilerConfig getConfig();

    /**
     * Add a {@link ApplicationTypeDetector} to ELAgent agent.
     * 
     * @param detectors
     */
    void addApplicationTypeDetector(ApplicationTypeDetector... detectors);

    void addJdbcUrlParser(JdbcUrlParserV2 jdbcUrlParserV2);

}