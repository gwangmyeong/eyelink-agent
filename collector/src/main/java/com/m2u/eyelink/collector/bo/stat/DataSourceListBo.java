package com.m2u.eyelink.collector.bo.stat;

import java.util.ArrayList;
import java.util.List;

public class DataSourceListBo implements AgentStatDataPointList<DataSourceBo> {

    private final List<DataSourceBo> dataSourceBoList = new ArrayList<DataSourceBo>();

    private String agentId;
    private long startTimestamp;
    private long timestamp;

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
        return AgentStatType.DATASOURCE;
    }

    @Override
    public boolean add(DataSourceBo element) {
        return dataSourceBoList.add(element);
    }

    @Override
    public boolean remove(DataSourceBo element) {
        return dataSourceBoList.remove(element);
    }

    @Override
    public int size() {
        if (dataSourceBoList == null) {
            return 0;
        }

        return dataSourceBoList.size();
    }

    @Override
    public List<DataSourceBo> getList() {
        return new ArrayList<DataSourceBo>(dataSourceBoList);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataSourceListBo that = (DataSourceListBo) o;

        if (startTimestamp != that.startTimestamp) return false;
        if (timestamp != that.timestamp) return false;
        if (dataSourceBoList != null ? !dataSourceBoList.equals(that.dataSourceBoList) : that.dataSourceBoList != null) return false;
        return agentId != null ? agentId.equals(that.agentId) : that.agentId == null;

    }

    @Override
    public int hashCode() {
        int result = dataSourceBoList != null ? dataSourceBoList.hashCode() : 0;
        result = 31 * result + (agentId != null ? agentId.hashCode() : 0);
        result = 31 * result + (int) (startTimestamp ^ (startTimestamp >>> 32));
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DataSourceListBo{");
        sb.append("dataSourceBoList=").append(dataSourceBoList);
        sb.append(", agentId='").append(agentId).append('\'');
        sb.append(", startTimestamp=").append(startTimestamp);
        sb.append(", timestamp=").append(timestamp);
        sb.append('}');
        return sb.toString();
    }

}
