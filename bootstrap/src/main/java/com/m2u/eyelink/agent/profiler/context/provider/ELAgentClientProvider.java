package com.m2u.eyelink.agent.profiler.context.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.m2u.eyelink.config.ProfilerConfig;
import com.m2u.eyelink.rpc.client.ELAgentClient;
import com.m2u.eyelink.rpc.client.ELAgentClientFactory;
import com.m2u.eyelink.rpc.util.ClientFactoryUtils;

public class ELAgentClientProvider implements Provider<ELAgentClient> {
    private final ProfilerConfig profilerConfig;
    private final Provider<ELAgentClientFactory> clientFactory;

    @Inject
    public ELAgentClientProvider(ProfilerConfig profilerConfig, Provider<ELAgentClientFactory> clientFactory) {
        if (profilerConfig == null) {
            throw new NullPointerException("profilerConfig must not be null");
        }
        if (clientFactory == null) {
            throw new NullPointerException("clientFactory must not be null");
        }
        this.profilerConfig = profilerConfig;
        this.clientFactory = clientFactory;
    }

    @Override
    public ELAgentClient get() {
        ELAgentClientFactory elagentClientFactory = clientFactory.get();
        ELAgentClient elagentClient = ClientFactoryUtils.createELAgentClient(profilerConfig.getCollectorTcpServerIp(), profilerConfig.getCollectorTcpServerPort(), elagentClientFactory);
        return elagentClient;
    }
}