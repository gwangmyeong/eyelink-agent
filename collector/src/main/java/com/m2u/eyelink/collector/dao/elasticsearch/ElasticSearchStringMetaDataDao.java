package com.m2u.eyelink.collector.dao.elasticsearch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.m2u.eyelink.collector.bo.StringMetaDataBo;
import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchOperations2;
import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchTables;
import com.m2u.eyelink.collector.common.elasticsearch.Put;
import com.m2u.eyelink.collector.common.elasticsearch.RowMapper;
import com.m2u.eyelink.collector.dao.StringMetaDataDao;
import com.m2u.eyelink.collector.util.ElasticSearchUtils;
import com.m2u.eyelink.thrift.dto.TStringMetaData;

@Repository
public class ElasticSearchStringMetaDataDao implements StringMetaDataDao {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ElasticSearchOperations2 elasticSearchTemplate;

    @Autowired
    @Qualifier("metadataRowKeyDistributor")
    private RowKeyDistributorByHashPrefix rowKeyDistributorByHashPrefix;

    @Autowired
    @Qualifier("stringMetaDataMapper")
    private RowMapper<List<StringMetaDataBo>> stringMetaDataMapper;
    
    @Override
    public void insert(TStringMetaData stringMetaData) {
        if (stringMetaData == null) {
            throw new NullPointerException("stringMetaData must not be null");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("insert:{}", stringMetaData);
        }

        final StringMetaDataBo stringMetaDataBo = new StringMetaDataBo(stringMetaData.getAgentId(), stringMetaData.getAgentStartTime(), stringMetaData.getStringId());
        final byte[] rowKey = getDistributedKey(stringMetaDataBo.toRowKey());


        Put put = new Put(rowKey);
        String stringValue = stringMetaData.getStringValue();
        byte[] sqlBytes = Bytes.toBytes(stringValue);
        put.addColumn(ElasticSearchTables.STRING_METADATA_CF_STR, ElasticSearchTables.STRING_METADATA_CF_STR_QUALI_STRING, sqlBytes);

//        elasticSearchTemplate.put(ElasticSearchTables.STRING_METADATA, put);
        this.elasticSearchTemplate.put(ElasticSearchUtils.generateIndexName(stringMetaDataBo.getAgentId(), ElasticSearchTables.TYPE_STRING_METADATA),
				ElasticSearchTables.TYPE_STRING_METADATA, stringMetaDataBo.getMap());
        
    }

    private byte[] getDistributedKey(byte[] rowKey) {
        return rowKeyDistributorByHashPrefix.getDistributedKey(rowKey);
    }

	@Override
	public List<StringMetaDataBo> getStringMetaData(String agentId, long agentStartTime, int stringMetaDataId) {
        if (agentId == null) {
            throw new NullPointerException("agentId must not be null");
        }

        Map<String, Object> cond = new HashMap<String, Object>();
        cond.put("agentId", agentId);
        cond.put("agentStartTime", agentStartTime);
        cond.put("stringMetaDataId", stringMetaDataId);
        return elasticSearchTemplate.get(ElasticSearchUtils.generateIndexName(agentId, ElasticSearchTables.TYPE_STRING_METADATA), ElasticSearchTables.TYPE_STRING_METADATA,  cond, stringMetaDataMapper);
	}
}
