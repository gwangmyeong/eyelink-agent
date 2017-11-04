package com.m2u.eyelink.collector.cluster;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TBase;
import org.springframework.util.NumberUtils;

import com.m2u.eyelink.collector.util.MapUtils;
import com.m2u.eyelink.context.HandshakePropertyType;
import com.m2u.eyelink.rpc.Future;
import com.m2u.eyelink.rpc.server.ELAgentServer;
import com.m2u.eyelink.thrift.TCommandType;
import com.m2u.eyelink.thrift.TCommandTypeVersion;
import com.m2u.eyelink.util.AssertUtils;

public class ELAgentServerClusterPoint implements TargetClusterPoint {

    private final ELAgentServer elagentServer;

    private final String applicationName;
    private final String agentId;
    private final long startTimeStamp;

    private final String version;
    private final List<TCommandType> supportCommandList;

    public ELAgentServerClusterPoint(ELAgentServer elagentServer) {
        AssertUtils.assertNotNull(elagentServer, "elagentServer may not be null.");
        this.elagentServer = elagentServer;

        Map<Object, Object> properties = elagentServer.getChannelProperties();
        this.version = MapUtils.getString(properties, HandshakePropertyType.VERSION.getName());
        AssertUtils.assertTrue(!StringUtils.isBlank(version), "Version may not be null or empty.");

        this.supportCommandList = new ArrayList<>();
        Object supportCommandCodeList = properties.get(HandshakePropertyType.SUPPORT_COMMAND_LIST.getName());
        if (supportCommandCodeList instanceof List) {
            for (Object supportCommandCode : (List)supportCommandCodeList) {
                if (supportCommandCode instanceof Number) {
                    TCommandType commandType = TCommandType.getType(NumberUtils.convertNumberToTargetClass((Number) supportCommandCode, Short.class));
                    if (commandType != null) {
                        supportCommandList.add(commandType);
                    }
                }
            }
        }

        this.applicationName = MapUtils.getString(properties, HandshakePropertyType.APPLICATION_NAME.getName());
        AssertUtils.assertTrue(!StringUtils.isBlank(applicationName), "ApplicationName may not be null or empty.");

        this.agentId = MapUtils.getString(properties, HandshakePropertyType.AGENT_ID.getName());
        AssertUtils.assertTrue(!StringUtils.isBlank(agentId), "AgentId may not be null or empty.");

        this.startTimeStamp = MapUtils.getLong(properties, HandshakePropertyType.START_TIMESTAMP.getName());
        AssertUtils.assertTrue(startTimeStamp > 0, "StartTimeStamp is must greater than zero.");
    }

    @Override
    public void send(byte[] payload) {
        elagentServer.send(payload);
    }

    @Override
    public Future request(byte[] payload) {
        return elagentServer.request(payload);
    }

    @Override
    public String getApplicationName() {
        return applicationName;
    }

    @Override
    public String getAgentId() {
        return agentId;
    }

    public long getStartTimeStamp() {
        return startTimeStamp;
    }

    @Override
    public String gerVersion() {
        return version;
    }

    @Override
    public boolean isSupportCommand(TBase command) {
        for (TCommandType supportCommand : supportCommandList) {
            if (supportCommand.getClazz() == command.getClass()) {
                return true;
            }
        }

        TCommandTypeVersion commandVersion = TCommandTypeVersion.getVersion(version);
        if (commandVersion.isSupportCommand(command)) {
            return true;
        }

        return false;
    }

    public ELAgentServer getELAgentServer() {
        return elagentServer;
    }
    
    @Override
    public String toString() {
        StringBuilder log = new StringBuilder(32);
        log.append(this.getClass().getSimpleName());
        log.append("(");
        log.append(applicationName);
        log.append("/");
        log.append(agentId);
        log.append("/");
        log.append(startTimeStamp);
        log.append(")");
        log.append(", version:");
        log.append(version);
        log.append(", supportCommandList:");
        log.append(supportCommandList);
        log.append(", pinpointServer:");
        log.append(elagentServer);
        
        return log.toString();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 17;
        
        result = prime * result + ((applicationName == null) ? 0 : applicationName.hashCode());
        result = prime * result + ((agentId == null) ? 0 : agentId.hashCode());
        result = prime * result + (int) (startTimeStamp ^ (startTimeStamp >>> 32));
        result = prime * result + ((version == null) ? 0 : version.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof ELAgentServerClusterPoint)) {
            return false;
        }

        if (this.getELAgentServer() == ((ELAgentServerClusterPoint) obj).getELAgentServer()) {
            return true;
        }

        return false;
    }

}
