package com.m2u.eyelink.agent.profiler.context.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.m2u.eyelink.agent.profiler.DefaultAgentInformation;
import com.m2u.eyelink.agent.profiler.context.module.AgentId;
import com.m2u.eyelink.agent.profiler.context.module.AgentStartTime;
import com.m2u.eyelink.agent.profiler.context.module.ApplicationName;
import com.m2u.eyelink.agent.profiler.context.module.ApplicationServerType;
import com.m2u.eyelink.common.Version;
import com.m2u.eyelink.context.AgentInformation;
import com.m2u.eyelink.context.RuntimeMXBeanUtils;
import com.m2u.eyelink.common.trace.ServiceType;
import com.m2u.eyelink.util.IdValidateUtils;
import com.m2u.eyelink.util.JvmUtils;
import com.m2u.eyelink.util.NetworkUtils;
import com.m2u.eyelink.util.SystemPropertyKey;

public class AgentInformationProvider implements Provider<AgentInformation> {

    private final String agentId;
    private final String applicationName;
    private final long agentStartTime;
    private final ServiceType serverType;

    @Inject
    public AgentInformationProvider(@AgentId String agentId, @ApplicationName String applicationName, @AgentStartTime long agentStartTime, @ApplicationServerType ServiceType serverType) {
        if (agentId == null) {
            throw new NullPointerException("agentId must not be null");
        }
        if (applicationName == null) {
            throw new NullPointerException("applicationName must not be null");
        }
        if (serverType == null) {
            throw new NullPointerException("serverType must not be null");
        }

        this.agentId = checkId("agentId", agentId);
        this.applicationName = checkId("applicationName", applicationName);
        this.agentStartTime = agentStartTime;
        this.serverType = serverType;

    }

    public AgentInformation get() {
        return createAgentInformation();
    }

    public AgentInformation createAgentInformation() {

        final String machineName = NetworkUtils.getHostName();
        final String hostIp = NetworkUtils.getRepresentationHostIp();

        final int pid = RuntimeMXBeanUtils.getPid();
        final String jvmVersion = JvmUtils.getSystemProperty(SystemPropertyKey.JAVA_VERSION);
        return new DefaultAgentInformation(agentId, applicationName, agentStartTime, pid, machineName, hostIp, serverType, jvmVersion, Version.VERSION);
    }

    private String checkId(String keyName,String id) {
        if (!IdValidateUtils.validateId(id)) {
            throw new IllegalArgumentException("invalid " + keyName + "=" + id);
        }
        return id;
    }
}
