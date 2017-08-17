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
	
    public static final TableName APPLICATION_INDEX = TableName.valueOf("ApplicationIndex");
    public static final byte[] APPLICATION_INDEX_CF_AGENTS = Bytes.toBytes("Agents");


    public static final TableName SQL_METADATA_VER2 = TableName.valueOf("SqlMetaData_Ver2");
    public static final byte[] SQL_METADATA_VER2_CF_SQL = Bytes.toBytes("Sql");
    public static final byte[] SQL_METADATA_VER2_CF_SQL_QUALI_SQLSTATEMENT = Bytes.toBytes("P_sql_statement");
    
    
	public static final byte[] API_METADATA_CF_API = Bytes.toBytes("Api");
	public static final byte[] API_METADATA_CF_API_QUALI_SIGNATURE = Bytes.toBytes("P_api_signature");
	public static final TableName API_METADATA = TableName.valueOf("ApiMetaData");
	
    public static final TableName STRING_METADATA = TableName.valueOf("StringMetaData");
    public static final byte[] STRING_METADATA_CF_STR = Bytes.toBytes("Str");
    public static final byte[] STRING_METADATA_CF_STR_QUALI_STRING = Bytes.toBytes("P_string");
    
    public static final TableName AGENT_EVENT = TableName.valueOf("AgentEvent");
    public static final byte[] AGENT_EVENT_CF_EVENTS = Bytes.toBytes("E"); // agent events column family
    
    public static final TableName AGENT_LIFECYCLE = TableName.valueOf("AgentLifeCycle");
    public static final byte[] AGENT_LIFECYCLE_CF_STATUS = Bytes.toBytes("S"); // agent lifecycle column family
    public static final byte[] AGENT_LIFECYCLE_CF_STATUS_QUALI_STATES = Bytes.toBytes("states"); // qualifier for agent lifecycle states
    
    // for ElasticSearch
    public static final String IndexNamePrefix = "elagent";  	// elagent_[agentid]-[날짜]
    public static final String TYPE_AGENTINFO = "AgentInfo";


}
