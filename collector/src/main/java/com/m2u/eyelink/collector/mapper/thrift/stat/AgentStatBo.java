package com.m2u.eyelink.collector.mapper.thrift.stat;

import java.util.List;

import com.m2u.eyelink.collector.bo.stat.ActiveTraceBo;
import com.m2u.eyelink.collector.bo.stat.CpuLoadBo;
import com.m2u.eyelink.collector.bo.stat.DataSourceListBo;
import com.m2u.eyelink.collector.bo.stat.JvmGcBo;
import com.m2u.eyelink.collector.bo.stat.JvmGcDetailedBo;
import com.m2u.eyelink.collector.bo.stat.TransactionBo;

public class AgentStatBo {

    private String agentId;
    private List<JvmGcBo> jvmGcBos;
    private List<JvmGcDetailedBo> jvmGcDetailedBos;
    private List<CpuLoadBo> cpuLoadBos;
    private List<TransactionBo> transactionBos;
    private List<ActiveTraceBo> activeTraceBos;
    private List<DataSourceListBo> dataSourceListBos;

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public List<JvmGcBo> getJvmGcBos() {
        return jvmGcBos;
    }

    public void setJvmGcBos(List<JvmGcBo> jvmGcBos) {
        this.jvmGcBos = jvmGcBos;
    }

    public List<JvmGcDetailedBo> getJvmGcDetailedBos() {
        return jvmGcDetailedBos;
    }

    public void setJvmGcDetailedBos(List<JvmGcDetailedBo> jvmGcDetailedBos) {
        this.jvmGcDetailedBos = jvmGcDetailedBos;
    }

    public List<CpuLoadBo> getCpuLoadBos() {
        return cpuLoadBos;
    }

    public void setCpuLoadBos(List<CpuLoadBo> cpuLoadBos) {
        this.cpuLoadBos = cpuLoadBos;
    }

    public List<TransactionBo> getTransactionBos() {
        return transactionBos;
    }

    public void setTransactionBos(List<TransactionBo> transactionBos) {
        this.transactionBos = transactionBos;
    }

    public List<ActiveTraceBo> getActiveTraceBos() {
        return activeTraceBos;
    }

    public void setActiveTraceBos(List<ActiveTraceBo> activeTraceBos) {
        this.activeTraceBos = activeTraceBos;
    }

    public List<DataSourceListBo> getDataSourceListBos() {
        return dataSourceListBos;
    }

    public void setDataSourceListBos(List<DataSourceListBo> dataSourceListBos) {
        this.dataSourceListBos = dataSourceListBos;
    }

    @Override
    public String toString() {
        return "AgentStatBatchBo{" +
                "agentId='" + agentId + '\'' +
                ", jvmGcBos=" + jvmGcBos +
                ", jvmGcDetailedBos=" + jvmGcDetailedBos +
                ", cpuLoadBos=" + cpuLoadBos +
                ", transactionBos=" + transactionBos +
                ", activeTraceBos=" + activeTraceBos +
                ", dataSourceListBos=" + dataSourceListBos +
                '}';
    }

}
