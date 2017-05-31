package com.m2u.eyelink.agent.profiler.context.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.m2u.eyelink.agent.profiler.context.module.AgentId;
import com.m2u.eyelink.agent.profiler.context.module.AgentStartTime;
import com.m2u.eyelink.agent.profiler.metadata.ApiMetaDataService;
import com.m2u.eyelink.agent.profiler.metadata.DefaultApiMetaDataService;
import com.m2u.eyelink.sender.EnhancedDataSender;

public class ApiMetaDataServiceProvider implements Provider<ApiMetaDataService> {
    private final String agentId;
    private final long agentStartTime;
    private final Provider<EnhancedDataSender> enhancedDataSenderProvider;

    @Inject
    public ApiMetaDataServiceProvider(@AgentId String agentId, @AgentStartTime long agentStartTime, Provider<EnhancedDataSender> enhancedDataSenderProvider) {
        if (enhancedDataSenderProvider == null) {
            throw new NullPointerException("enhancedDataSenderProvider must not be null");
        }
        this.agentId = agentId;
        this.agentStartTime = agentStartTime;
        this.enhancedDataSenderProvider = enhancedDataSenderProvider;
    }

    @Override
    public ApiMetaDataService get() {
        final EnhancedDataSender enhancedDataSender = this.enhancedDataSenderProvider.get();
        return new DefaultApiMetaDataService(agentId, agentStartTime, enhancedDataSender);
    }
}
