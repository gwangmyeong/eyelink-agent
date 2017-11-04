package com.m2u.eyelink.agent.profiler.metadata;

import com.google.inject.Inject;
import com.m2u.eyelink.agent.profiler.context.module.AgentId;
import com.m2u.eyelink.agent.profiler.context.module.AgentStartTime;
import com.m2u.eyelink.sender.EnhancedDataSender;
import com.m2u.eyelink.thrift.dto.TStringMetaData;

public class DefaultStringMetaDataService implements StringMetaDataService {

    private final SimpleCache<String> stringCache = new SimpleCache<String>();

    private final String agentId;
    private final long agentStartTime;
    private final EnhancedDataSender enhancedDataSender;

    @Inject
    public DefaultStringMetaDataService(@AgentId String agentId, @AgentStartTime long agentStartTime, EnhancedDataSender enhancedDataSender) {
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
    public int cacheString(final String value) {
        if (value == null) {
            return 0;
        }
        final Result result = this.stringCache.put(value);
        if (result.isNewValue()) {
            final TStringMetaData stringMetaData = new TStringMetaData();
            stringMetaData.setAgentId(agentId);
            stringMetaData.setAgentStartTime(agentStartTime);

            stringMetaData.setStringId(result.getId());
            stringMetaData.setStringValue(value);
            this.enhancedDataSender.request(stringMetaData);
        }
        return result.getId();
    }
}
