package com.m2u.eyelink.agent.profiler.context.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.m2u.eyelink.agent.profiler.sender.TcpDataSender;
import com.m2u.eyelink.rpc.client.ELAgentClient;
import com.m2u.eyelink.sender.EnhancedDataSender;

public class TcpDataSenderProvider implements Provider<EnhancedDataSender> {
    private final Provider<ELAgentClient> client;

    @Inject
    public TcpDataSenderProvider(Provider<ELAgentClient> client) {
        if (client == null) {
            throw new NullPointerException("client must not be null");
        }

        this.client = client;
    }

    @Override
    public EnhancedDataSender get() {
    	ELAgentClient elagentClient = client.get();
        return new TcpDataSender(elagentClient);
    }
}