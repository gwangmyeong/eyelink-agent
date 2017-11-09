package com.m2u.eyelink.agent.profiler.context.provider;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.m2u.eyelink.agent.profiler.receiver.CommandDispatcher;
import com.m2u.eyelink.config.ProfilerConfig;
import com.m2u.eyelink.context.AgentInformation;
import com.m2u.eyelink.context.HandshakePropertyType;
import com.m2u.eyelink.rpc.client.DefaultELAgentClientFactory;
import com.m2u.eyelink.rpc.client.ELAgentClientFactory;

public class ELAgentClientFactoryProvider implements Provider<ELAgentClientFactory> {

    private final ProfilerConfig profilerConfig;
    private final Provider<AgentInformation> agentInformation;
    private final CommandDispatcher commandDispatcher;

    @Inject
    public ELAgentClientFactoryProvider(ProfilerConfig profilerConfig, Provider<AgentInformation> agentInformation, CommandDispatcher commandDispatcher) {
        if (profilerConfig == null) {
            throw new NullPointerException("profilerConfig must not be null");
        }
        if (agentInformation == null) {
            throw new NullPointerException("agentInformation must not be null");
        }
        if (commandDispatcher == null) {
            throw new NullPointerException("commandDispatcher must not be null");
        }
        this.profilerConfig = profilerConfig;
        this.agentInformation = agentInformation;
        this.commandDispatcher = commandDispatcher;
    }

    public ELAgentClientFactory get() {
    	ELAgentClientFactory elagentClientFactory = new DefaultELAgentClientFactory();
        elagentClientFactory.setTimeoutMillis(1000 * 5);

        AgentInformation agentInformation = this.agentInformation.get();
        Map<String, Object> properties = toMap(agentInformation);

        boolean isSupportServerMode = profilerConfig.isTcpDataSenderCommandAcceptEnable();

        if (isSupportServerMode) {
            elagentClientFactory.setMessageListener(commandDispatcher);
            elagentClientFactory.setServerStreamChannelMessageListener(commandDispatcher);

            properties.put(HandshakePropertyType.SUPPORT_SERVER.getName(), true);
            properties.put(HandshakePropertyType.SUPPORT_COMMAND_LIST.getName(), commandDispatcher.getRegisteredCommandServiceCodes());
        } else {
            properties.put(HandshakePropertyType.SUPPORT_SERVER.getName(), false);
        }

        elagentClientFactory.setProperties(properties);
        return elagentClientFactory;

    }

    private Map<String, Object> toMap(AgentInformation agentInformation) {
        Map<String, Object> map = new HashMap<String, Object>();

        map.put(HandshakePropertyType.AGENT_ID.getName(), agentInformation.getAgentId());
        map.put(HandshakePropertyType.APPLICATION_NAME.getName(), agentInformation.getApplicationName());
        map.put(HandshakePropertyType.HOSTNAME.getName(), agentInformation.getMachineName());
        map.put(HandshakePropertyType.IP.getName(), agentInformation.getHostIp());
        map.put(HandshakePropertyType.PID.getName(), agentInformation.getPid());
        map.put(HandshakePropertyType.SERVICE_TYPE.getName(), agentInformation.getServerType().getCode());
        map.put(HandshakePropertyType.START_TIMESTAMP.getName(), agentInformation.getStartTime());
        map.put(HandshakePropertyType.VERSION.getName(), agentInformation.getAgentVersion());

        return map;
    }
}
