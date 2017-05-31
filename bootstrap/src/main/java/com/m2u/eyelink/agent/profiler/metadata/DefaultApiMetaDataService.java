package com.m2u.eyelink.agent.profiler.metadata;

import com.m2u.eyelink.agent.profiler.context.module.AgentId;
import com.m2u.eyelink.agent.profiler.context.module.AgentStartTime;
import com.m2u.eyelink.context.MethodDescriptor;
import com.m2u.eyelink.context.SimpleCache;
import com.m2u.eyelink.context.TApiMetaData;
import com.m2u.eyelink.sender.EnhancedDataSender;

public class DefaultApiMetaDataService implements ApiMetaDataService {

    private final SimpleCache<String> apiCache = new SimpleCache<String>();

    private final String agentId;
    private final long agentStartTime;
    private final EnhancedDataSender enhancedDataSender;

    public DefaultApiMetaDataService(@AgentId String agentId, @AgentStartTime long agentStartTime, EnhancedDataSender enhancedDataSender) {
        if (agentId == null) {
            throw new NullPointerException("agentId must not be null");
        }
        if (enhancedDataSender == null) {
            throw new NullPointerException("enhancedDataSender must not be null");
        }
        this.agentId = agentId;
        this.agentStartTime = agentStartTime;
        this.enhancedDataSender = enhancedDataSender;
    }

    @Override
    public int cacheApi(final MethodDescriptor methodDescriptor) {
        final String fullName = methodDescriptor.getFullName();
        final Result result = this.apiCache.put(fullName);

        methodDescriptor.setApiId(result.getId());

        if (result.isNewValue()) {
            final TApiMetaData apiMetadata = new TApiMetaData();
            apiMetadata.setAgentId(agentId);
            apiMetadata.setAgentStartTime(agentStartTime);

            apiMetadata.setApiId(result.getId());
            apiMetadata.setApiInfo(methodDescriptor.getApiDescriptor());
            apiMetadata.setLine(methodDescriptor.getLineNumber());
            apiMetadata.setType(methodDescriptor.getType());

            this.enhancedDataSender.request(apiMetadata);
        }

        return result.getId();
    }
}
