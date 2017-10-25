package com.m2u.eyelink.agent.profiler.context.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.m2u.eyelink.agent.profiler.AgentInfoSender;
import com.m2u.eyelink.agent.profiler.JvmInformation;
import com.m2u.eyelink.config.ProfilerConfig;
import com.m2u.eyelink.context.AgentInformation;
import com.m2u.eyelink.sender.EnhancedDataSender;

public class AgentInfoSenderProvider implements Provider<AgentInfoSender> {

    private final ProfilerConfig profilerConfig;
    private final Provider<EnhancedDataSender> enhancedDataSenderProvider;
    private final Provider<AgentInformation> agentInformationProvider;
    private final JvmInformation jvmInformation;

    @Inject
    public AgentInfoSenderProvider(ProfilerConfig profilerConfig, Provider<EnhancedDataSender> enhancedDataSenderProvider, Provider<AgentInformation> agentInformationProvider, JvmInformation jvmInformation) {
        if (profilerConfig == null) {
            throw new NullPointerException("profilerConfig must not be null");
        }
        if (enhancedDataSenderProvider == null) {
            throw new NullPointerException("enhancedDataSenderProvider must not be null");
        }
        if (agentInformationProvider == null) {
            throw new NullPointerException("agentInformationProvider must not be null");
        }
        if (jvmInformation == null) {
            throw new NullPointerException("jvmInformation must not be null");
        }

        this.profilerConfig = profilerConfig;
        this.enhancedDataSenderProvider = enhancedDataSenderProvider;
        this.agentInformationProvider = agentInformationProvider;
        this.jvmInformation = jvmInformation;
    }

    @Override
    public AgentInfoSender get() {
        final EnhancedDataSender enhancedDataSender = this.enhancedDataSenderProvider.get();
        final AgentInformation agentInformation = this.agentInformationProvider.get();
        final AgentInfoSender.Builder builder = new AgentInfoSender.Builder(enhancedDataSender, agentInformation, jvmInformation);
        builder.sendInterval(profilerConfig.getAgentInfoSendRetryInterval());
        return builder.build();
    }
}
