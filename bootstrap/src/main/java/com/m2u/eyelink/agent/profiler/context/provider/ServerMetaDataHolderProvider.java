package com.m2u.eyelink.agent.profiler.context.provider;

import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.m2u.eyelink.agent.profiler.AgentInfoSender;
import com.m2u.eyelink.context.DefaultServerMetaDataHolder;
import com.m2u.eyelink.context.RuntimeMXBeanUtils;
import com.m2u.eyelink.context.ServerMetaDataHolder;

public class ServerMetaDataHolderProvider implements Provider<ServerMetaDataHolder> {

    private final Provider<AgentInfoSender> agentInfoSender;

    @Inject
    public ServerMetaDataHolderProvider(Provider<AgentInfoSender> agentInfoSender) {
        this.agentInfoSender = agentInfoSender;
    }

    @Override
    public ServerMetaDataHolder get() {
        AgentInfoSender agentInfoSender = this.agentInfoSender.get();
        List<String> vmArgs = RuntimeMXBeanUtils.getVmArgs();
        ServerMetaDataHolder serverMetaDataHolder = new DefaultServerMetaDataHolder(vmArgs);
        serverMetaDataHolder.addListener(agentInfoSender);
        return serverMetaDataHolder;
    }

}
