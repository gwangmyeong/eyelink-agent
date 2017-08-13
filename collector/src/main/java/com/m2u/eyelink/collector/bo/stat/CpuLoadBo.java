package com.m2u.eyelink.collector.bo.stat;

public class CpuLoadBo implements AgentStatDataPoint {

    public static final double UNCOLLECTED_VALUE = -1;

    private String agentId;
    private long startTimestamp;
    private long timestamp;
    private double jvmCpuLoad = UNCOLLECTED_VALUE;
    private double systemCpuLoad = UNCOLLECTED_VALUE;

    @Override
    public String getAgentId() {
        return agentId;
    }

    @Override
    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    @Override
    public long getStartTimestamp() {
        return startTimestamp;
    }

    @Override
    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public AgentStatType getAgentStatType() {
        return AgentStatType.CPU_LOAD;
    }

    public double getJvmCpuLoad() {
        return jvmCpuLoad;
    }

    public void setJvmCpuLoad(double jvmCpuLoad) {
        this.jvmCpuLoad = jvmCpuLoad;
    }

    public double getSystemCpuLoad() {
        return systemCpuLoad;
    }

    public void setSystemCpuLoad(double systemCpuLoad) {
        this.systemCpuLoad = systemCpuLoad;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CpuLoadBo cpuLoadBo = (CpuLoadBo) o;

        if (startTimestamp != cpuLoadBo.startTimestamp) return false;
        if (timestamp != cpuLoadBo.timestamp) return false;
        if (Double.compare(cpuLoadBo.jvmCpuLoad, jvmCpuLoad) != 0) return false;
        if (Double.compare(cpuLoadBo.systemCpuLoad, systemCpuLoad) != 0) return false;
        return agentId != null ? agentId.equals(cpuLoadBo.agentId) : cpuLoadBo.agentId == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = agentId != null ? agentId.hashCode() : 0;
        result = 31 * result + (int) (startTimestamp ^ (startTimestamp >>> 32));
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        temp = Double.doubleToLongBits(jvmCpuLoad);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(systemCpuLoad);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "CpuLoadBo{" +
                "agentId='" + agentId + '\'' +
                ", startTimestamp=" + startTimestamp +
                ", timestamp=" + timestamp +
                ", jvmCpuLoad=" + jvmCpuLoad +
                ", systemCpuLoad=" + systemCpuLoad +
                '}';
    }
}
