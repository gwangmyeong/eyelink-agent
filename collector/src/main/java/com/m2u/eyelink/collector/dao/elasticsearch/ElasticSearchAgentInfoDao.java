package com.m2u.eyelink.collector.dao.elasticsearch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.m2u.eyelink.collector.bo.AgentInfoBo;
import com.m2u.eyelink.collector.bo.JvmInfoBo;
import com.m2u.eyelink.collector.bo.ServerMetaDataBo;
import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchOperations2;
import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchTables;
import com.m2u.eyelink.collector.common.elasticsearch.Put;
import com.m2u.eyelink.collector.dao.AgentInfoDao;
import com.m2u.eyelink.collector.mapper.thrift.ThriftBoMapper;
import com.m2u.eyelink.collector.util.ElasticSearchUtils;
import com.m2u.eyelink.collector.util.RowKeyUtils;
import com.m2u.eyelink.collector.util.TimeUtils;
import com.m2u.eyelink.thrift.TAgentInfo;
import com.m2u.eyelink.thrift.TJvmInfo;
import com.m2u.eyelink.thrift.TServerMetaData;

@Repository
public class ElasticSearchAgentInfoDao implements AgentInfoDao {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ElasticSearchOperations2 elasticSearchTemplate;

    @Autowired
    @Qualifier("agentInfoBoMapper")
    private ThriftBoMapper<AgentInfoBo, TAgentInfo> agentInfoBoMapper;

    @Autowired
    @Qualifier("serverMetaDataBoMapper")
    private ThriftBoMapper<ServerMetaDataBo, TServerMetaData> serverMetaDataBoMapper;

    @Autowired
    @Qualifier("jvmInfoBoMapper")
    private ThriftBoMapper<JvmInfoBo, TJvmInfo> jvmInfoBoMapper;

    @Override
    public void insert(TAgentInfo agentInfo) {

        if (agentInfo == null) {
            throw new NullPointerException("agentInfo must not be null");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("insert agent info. {}", agentInfo);
        }

        byte[] agentId = Bytes.toBytes(agentInfo.getAgentId());
        long reverseKey = TimeUtils.reverseTimeMillis(agentInfo.getStartTimestamp());
//        byte[] rowKey = RowKeyUtils.concatFixedByteAndLong(agentId, ElasticSearchTables.AGENT_NAME_MAX_LEN, reverseKey);
        Put put = new Put(null);

        // should add additional agent informations. for now added only starttime for sqlMetaData
        AgentInfoBo agentInfoBo = this.agentInfoBoMapper.map(agentInfo);
        byte[] agentInfoBoValue = agentInfoBo.writeValue();
        put.addColumn(ElasticSearchTables.AGENTINFO_CF_INFO, ElasticSearchTables.AGENTINFO_CF_INFO_IDENTIFIER, agentInfoBoValue);

        // FIXME bsh, need to implement logic below comments
        if (agentInfo.isSetServerMetaData()) {
        		ServerMetaDataBo serverMetaDataBo = this.serverMetaDataBoMapper.map(agentInfo.getServerMetaData());
        		agentInfoBo.setServerMetaData(serverMetaDataBo);
            byte[] serverMetaDataBoValue = serverMetaDataBo.writeValue();
            put.addColumn(ElasticSearchTables.AGENTINFO_CF_INFO, ElasticSearchTables.AGENTINFO_CF_INFO_SERVER_META_DATA, serverMetaDataBoValue);
        }

        // FIXME, need to implement logic to save JvmInfo, bsh
        if (agentInfo.isSetJvmInfo()) {
            JvmInfoBo jvmInfoBo = this.jvmInfoBoMapper.map(agentInfo.getJvmInfo());
            agentInfoBo.setJvmInfo(jvmInfoBo);
            byte[] jvmInfoBoValue = jvmInfoBo.writeValue();
            put.addColumn(ElasticSearchTables.AGENTINFO_CF_INFO, ElasticSearchTables.AGENTINFO_CF_INFO_JVM, jvmInfoBoValue);
        }

//        elasticSearchTemplate.put(ElasticSearchTables.AGENTINFO, put);
        elasticSearchTemplate.put(ElasticSearchUtils.generateIndexName(agentInfo.getAgentId()), ElasticSearchTables.TYPE_AGENT_INFO, agentInfoBo.getMap());
    }
}
