package com.m2u.eyelink.collector.mapper.thrift;

import org.springframework.stereotype.Component;

import com.m2u.eyelink.collector.bo.AgentInfoBo;
import com.m2u.eyelink.thrift.TAgentInfo;
public class AgentInfoBoMapper implements ThriftBoMapper<AgentInfoBo, TAgentInfo> {

    @Override
    public AgentInfoBo map(TAgentInfo thriftObject) {
        final String hostName = thriftObject.getHostname();
        final String ip = thriftObject.getIp();
        final String ports = thriftObject.getPorts();
        final String agentId = thriftObject.getAgentId();
        final String applicationName = thriftObject.getApplicationName();
        final short serviceType = thriftObject.getServiceType();
        final int pid = thriftObject.getPid();
        final String vmVersion = thriftObject.getVmVersion();
        final String agentVersion = thriftObject.getAgentVersion();
        final long startTime = thriftObject.getStartTimestamp();
        final long endTimeStamp = thriftObject.getEndTimestamp();
        final int endStatus = thriftObject.getEndStatus();
        
        AgentInfoBo.Builder builder = new AgentInfoBo.Builder();
        builder.setHostName(hostName);
        builder.setIp(ip);
        builder.setPorts(ports);
        builder.setAgentId(agentId);
        builder.setApplicationName(applicationName);
        builder.setServiceTypeCode(serviceType);
        builder.setPid(pid);
        builder.setVmVersion(vmVersion);
        builder.setAgentVersion(agentVersion);
        builder.setStartTime(startTime);
        builder.setEndTimeStamp(endTimeStamp);
        builder.setEndStatus(endStatus);

        return builder.build();
    }

}
