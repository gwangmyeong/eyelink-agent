package com.m2u.eyelink.collector.bo;

import java.util.HashMap;
import java.util.Map;

import com.m2u.eyelink.collector.util.RowKeyUtils;
import com.m2u.eyelink.collector.util.TimeUtils;
import com.m2u.eyelink.util.BytesUtils;

import com.m2u.eyelink.common.ELAgentConstants;

public class SqlMetaDataBo {
    private String agentId;
    private long startTime;

    private int sqlId;

    private String sql;

    public SqlMetaDataBo() {
    }


    public SqlMetaDataBo(String agentId, long startTime, int sqlId) {
        if (agentId == null) {
            throw new NullPointerException("agentId must not be null");
        }
        this.agentId = agentId;
        this.sqlId = sqlId;
        this.startTime = startTime;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    /**
     * @deprecated Since 1.6.1. Use {@link #getSqlId()}
     */
    @Deprecated
    public int getHashCode() {
        return getSqlId();
    }


    /**
     * @deprecated Since 1.6.1. Use {@link #setSqlId(int)}
     */
    @Deprecated
    public void setHashCode(int sqlId) {
        this.setSqlId(sqlId);
    }

    public int getSqlId() {
        return sqlId;
    }

    public void setSqlId(int sqlId) {
        this.sqlId = sqlId;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public void readRowKey(byte[] rowKey) {
        this.agentId = BytesUtils.safeTrim(BytesUtils.toString(rowKey, 0, ELAgentConstants.AGENT_NAME_MAX_LEN));
        this.startTime = TimeUtils.recoveryTimeMillis(readTime(rowKey));
        this.sqlId = readSqlId(rowKey);
    }


    private static long readTime(byte[] rowKey) {
        return BytesUtils.bytesToLong(rowKey, ELAgentConstants.AGENT_NAME_MAX_LEN);
    }

    private static int readSqlId(byte[] rowKey) {
        return BytesUtils.bytesToInt(rowKey, ELAgentConstants.AGENT_NAME_MAX_LEN + BytesUtils.LONG_BYTE_LENGTH);
    }

    public byte[] toRowKey() {
        return RowKeyUtils.getMetaInfoRowKey(this.agentId, this.startTime, this.sqlId);
    }

    @Override
    public String toString() {
        return "SqlMetaDataBo{" +
                "agentId='" + agentId + '\'' +
                ", startTime=" + startTime +
                ", sqlId=" + sqlId +
                ", sql='" + sql + '\'' +
                '}';
    }
    
    public Map<String, Object> getMap() {
    		Map<String, Object> map = new HashMap<String, Object>();
    		map.put("agentId", this.agentId);
    		map.put("startTime", TimeUtils.convertEpochToDate(this.startTime));
    		map.put("sqlId", this.sqlId);
    		map.put("sql", this.sql);
    		
    		return map;
    	
    }

}
