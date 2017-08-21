package com.m2u.eyelink.collector.mapper.thrift.stat;

import java.util.ArrayList;
import java.util.List;

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
import com.m2u.eyelink.context.thrift.TAgentStat;
import com.m2u.eyelink.context.thrift.TAgentStatBatch;
import com.m2u.eyelink.context.thrift.TDataSource;
import com.m2u.eyelink.context.thrift.TDataSourceList;

@Component
public class AgentStatBatchMapper implements ThriftBoMapper<AgentStatBo, TAgentStatBatch> {

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
    public AgentStatBo map(TAgentStatBatch tAgentStatBatch) {
        if (!tAgentStatBatch.isSetAgentStats()) {
            return null;
        }
        AgentStatBo agentStatBo = new AgentStatBo();
        final String agentId = tAgentStatBatch.getAgentId();
        final long startTimestamp = tAgentStatBatch.getStartTimestamp();
        agentStatBo.setAgentId(agentId);
        List<JvmGcBo> jvmGcBos = new ArrayList<>(tAgentStatBatch.getAgentStatsSize());
        List<JvmGcDetailedBo> jvmGcDetailedBos = new ArrayList<>(tAgentStatBatch.getAgentStatsSize());
        List<CpuLoadBo> cpuLoadBos = new ArrayList<>(tAgentStatBatch.getAgentStatsSize());
        List<TransactionBo> transactionBos = new ArrayList<>(tAgentStatBatch.getAgentStatsSize());
        List<ActiveTraceBo> activeTraceBos = new ArrayList<>(tAgentStatBatch.getAgentStatsSize());
        List<DataSourceListBo> dataSourceListBos = new ArrayList<DataSourceListBo>(tAgentStatBatch.getAgentStatsSize());
        for (TAgentStat tAgentStat : tAgentStatBatch.getAgentStats()) {
            final long timestamp = tAgentStat.getTimestamp();
            // jvmGc
            if (tAgentStat.isSetGc()) {
                JvmGcBo jvmGcBo = this.jvmGcBoMapper.map(tAgentStat.getGc());
                setBaseData(jvmGcBo, agentId, startTimestamp, timestamp);
                jvmGcBos.add(jvmGcBo);
            }
            // jvmGcDetailed
            if (tAgentStat.isSetGc()) {
                if (tAgentStat.getGc().isSetJvmGcDetailed()) {
                    JvmGcDetailedBo jvmGcDetailedBo = this.jvmGcDetailedBoMapper.map(tAgentStat.getGc().getJvmGcDetailed());
                    setBaseData(jvmGcDetailedBo, agentId, startTimestamp, timestamp);
                    jvmGcDetailedBos.add(jvmGcDetailedBo);
                }
            }
            // cpuLoad
            if (tAgentStat.isSetCpuLoad()) {
                CpuLoadBo cpuLoadBo = this.cpuLoadBoMapper.map(tAgentStat.getCpuLoad());
                setBaseData(cpuLoadBo, agentId, startTimestamp, timestamp);
                cpuLoadBos.add(cpuLoadBo);
            }
            // transaction
            if (tAgentStat.isSetTransaction()) {
                TransactionBo transactionBo = this.transactionBoMapper.map(tAgentStat.getTransaction());
                setBaseData(transactionBo, agentId, startTimestamp, timestamp);
                transactionBo.setCollectInterval(tAgentStat.getCollectInterval());
                transactionBos.add(transactionBo);
            }
            // activeTrace
            if (tAgentStat.isSetActiveTrace() && tAgentStat.getActiveTrace().isSetHistogram()) {
                ActiveTraceBo activeTraceBo = this.activeTraceBoMapper.map(tAgentStat.getActiveTrace());
                setBaseData(activeTraceBo, agentId, startTimestamp, timestamp);
                activeTraceBos.add(activeTraceBo);
            }

            // datasource
            if (tAgentStat.isSetDataSourceList()) {
                DataSourceListBo dataSourceListBo = new DataSourceListBo();
                setBaseData(dataSourceListBo, agentId, startTimestamp, timestamp);

                TDataSourceList dataSourceList = tAgentStat.getDataSourceList();
                if (dataSourceList.getDataSourceListSize() > 0) {
                    for (TDataSource dataSource : dataSourceList.getDataSourceList()) {
                        DataSourceBo dataSourceBo = dataSourceBoMapper.map(dataSource);
                        setBaseData(dataSourceBo, agentId, startTimestamp, timestamp);
                        dataSourceListBo.add(dataSourceBo);
                    }
                }
                dataSourceListBos.add(dataSourceListBo);
            }
        }
        agentStatBo.setJvmGcBos(jvmGcBos);
        agentStatBo.setJvmGcDetailedBos(jvmGcDetailedBos);
        agentStatBo.setCpuLoadBos(cpuLoadBos);
        agentStatBo.setTransactionBos(transactionBos);
        agentStatBo.setActiveTraceBos(activeTraceBos);
        agentStatBo.setDataSourceListBos(dataSourceListBos);
        return agentStatBo;
    }

    private void setBaseData(AgentStatDataPoint agentStatDataPoint, String agentId, long startTimestamp, long timestamp) {
        agentStatDataPoint.setAgentId(agentId);
        agentStatDataPoint.setStartTimestamp(startTimestamp);
        agentStatDataPoint.setTimestamp(timestamp);
    }

}
