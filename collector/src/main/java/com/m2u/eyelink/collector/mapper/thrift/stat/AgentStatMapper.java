package com.m2u.eyelink.collector.mapper.thrift.stat;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.m2u.eyelink.collector.bo.stat.ActiveTraceBo;
import com.m2u.eyelink.collector.bo.stat.AgentStatDataPoint;
import com.m2u.eyelink.collector.bo.stat.CpuLoadBo;
import com.m2u.eyelink.collector.bo.stat.DataSourceBo;
import com.m2u.eyelink.collector.bo.stat.DataSourceListBo;
import com.m2u.eyelink.collector.bo.stat.JvmGcBo;
import com.m2u.eyelink.collector.bo.stat.JvmGcDetailedBo;
import com.m2u.eyelink.collector.bo.stat.TransactionBo;
import com.m2u.eyelink.collector.mapper.thrift.ThriftBoMapper;
import com.m2u.eyelink.thrift.TAgentStat;
import com.m2u.eyelink.thrift.TDataSource;
import com.m2u.eyelink.thrift.TDataSourceList;

public class AgentStatMapper implements ThriftBoMapper<AgentStatBo, TAgentStat> {

    @Autowired
    private JvmGcBoMapper jvmGcBoMapper;

    @Autowired
    private JvmGcDetailedBoMapper jvmGcDetailedBoMapper;

    @Autowired
    private CpuLoadBoMapper cpuLoadBoMapper;

    @Autowired
    private TransactionBoMapper transactionBoMapper;

    @Autowired
    private ActiveTraceBoMapper activeTraceBoMapper;

    @Autowired
    private DataSourceBoMapper dataSourceBoMapper;

    @Override
    public AgentStatBo map(TAgentStat tAgentStat) {
        if (tAgentStat == null) {
            return null;
        }
        final String agentId = tAgentStat.getAgentId();
        final long startTimestamp = tAgentStat.getStartTimestamp();
        final long timestamp = tAgentStat.getTimestamp();
        AgentStatBo agentStatBo = new AgentStatBo();
        agentStatBo.setAgentId(agentId);
        // jvmGc
        if (tAgentStat.isSetGc()) {
            JvmGcBo jvmGcBo = this.jvmGcBoMapper.map(tAgentStat.getGc());
            setBaseData(jvmGcBo, agentId, startTimestamp, timestamp);
            agentStatBo.setJvmGcBos(Arrays.asList(jvmGcBo));
        }
        // jvmGcDetailed
        if (tAgentStat.isSetGc()) {
            if (tAgentStat.getGc().isSetJvmGcDetailed()) {
                JvmGcDetailedBo jvmGcDetailedBo = this.jvmGcDetailedBoMapper.map(tAgentStat.getGc().getJvmGcDetailed());
                setBaseData(jvmGcDetailedBo, agentId, startTimestamp, timestamp);
                agentStatBo.setJvmGcDetailedBos(Arrays.asList(jvmGcDetailedBo));
            }
        }
        // cpuLoad
        if (tAgentStat.isSetCpuLoad()) {
            CpuLoadBo cpuLoadBo = this.cpuLoadBoMapper.map(tAgentStat.getCpuLoad());
            setBaseData(cpuLoadBo, agentId, startTimestamp, timestamp);
            agentStatBo.setCpuLoadBos(Arrays.asList(cpuLoadBo));
        }
        // transaction
        if (tAgentStat.isSetTransaction()) {
            TransactionBo transactionBo = this.transactionBoMapper.map(tAgentStat.getTransaction());
            setBaseData(transactionBo, agentId, startTimestamp, timestamp);
            transactionBo.setCollectInterval(tAgentStat.getCollectInterval());
            agentStatBo.setTransactionBos(Arrays.asList(transactionBo));
        }
        // activeTrace
        if (tAgentStat.isSetActiveTrace() && tAgentStat.getActiveTrace().isSetHistogram()) {
            ActiveTraceBo activeTraceBo = this.activeTraceBoMapper.map(tAgentStat.getActiveTrace());
            setBaseData(activeTraceBo, agentId, startTimestamp, timestamp);
            agentStatBo.setActiveTraceBos(Arrays.asList(activeTraceBo));
        }

        // datasource
        if (tAgentStat.isSetDataSourceList()) {
            DataSourceListBo dataSourceListBo = new DataSourceListBo();
            setBaseData(dataSourceListBo, agentId, startTimestamp, timestamp);

            TDataSourceList dataSourceList = tAgentStat.getDataSourceList();
            for (TDataSource dataSource : dataSourceList.getDataSourceList()) {
                DataSourceBo dataSourceBo = dataSourceBoMapper.map(dataSource);
                setBaseData(dataSourceBo, agentId, startTimestamp, timestamp);
                dataSourceListBo.add(dataSourceBo);
            }
            agentStatBo.setDataSourceListBos(Arrays.asList(dataSourceListBo));
        }

        return agentStatBo;
    }

    private void setBaseData(AgentStatDataPoint agentStatDataPoint, String agentId, long startTimestamp, long timestamp) {
        agentStatDataPoint.setAgentId(agentId);
        agentStatDataPoint.setStartTimestamp(startTimestamp);
        agentStatDataPoint.setTimestamp(timestamp);
    }

}
