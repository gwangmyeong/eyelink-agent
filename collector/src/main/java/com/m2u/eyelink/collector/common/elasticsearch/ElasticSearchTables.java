package com.m2u.eyelink.collector.common.elasticsearch;

import com.m2u.eyelink.collector.dao.elasticsearch.Bytes;
import com.m2u.eyelink.common.ELAgentConstants;

public class ElasticSearchTables {
	@Deprecated
	public static final TableName AGENT_STAT = TableName.valueOf("AgentStat");
	public static final TableName AGENT_STAT_VER2 = TableName.valueOf("AgentStatV2");
	
    public static final int APPLICATION_NAME_MAX_LEN = ELAgentConstants.APPLICATION_NAME_MAX_LEN;
    public static final int AGENT_NAME_MAX_LEN = ELAgentConstants.AGENT_NAME_MAX_LEN;
	
    public static final byte[] AGENTINFO_CF_INFO = Bytes.toBytes("Info");
	public static final byte[] AGENTINFO_CF_INFO_IDENTIFIER = Bytes.toBytes("i");
	public static final byte[] AGENTINFO_CF_INFO_SERVER_META_DATA = Bytes.toBytes("m");
	public static final byte[] AGENTINFO_CF_INFO_JVM = Bytes.toBytes("j");
	public static final TableName AGENTINFO = TableName.valueOf("AgentInfo");
}
