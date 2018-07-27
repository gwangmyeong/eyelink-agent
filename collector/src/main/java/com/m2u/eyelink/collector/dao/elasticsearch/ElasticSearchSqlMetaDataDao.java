package com.m2u.eyelink.collector.dao.elasticsearch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.m2u.eyelink.collector.bo.SqlMetaDataBo;
import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchOperations2;
import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchTables;
import com.m2u.eyelink.collector.common.elasticsearch.Put;
import com.m2u.eyelink.collector.common.elasticsearch.RowMapper;
import com.m2u.eyelink.collector.dao.SqlMetaDataDao;
import com.m2u.eyelink.collector.util.ElasticSearchUtils;
import com.m2u.eyelink.thrift.dto.TSqlMetaData;

public class ElasticSearchSqlMetaDataDao implements SqlMetaDataDao {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ElasticSearchOperations2 elasticSearchTemplate;

//  @Autowired
//  @Qualifier("sqlMetaDataMapper")
    private RowMapper<List<SqlMetaDataBo>> sqlMetaDataMapper;
  
    @Autowired
    @Qualifier("metadataRowKeyDistributor2")
    private RowKeyDistributorByHashPrefix rowKeyDistributorByHashPrefix;

    @Override
    public void insert(TSqlMetaData sqlMetaData) {
        if (sqlMetaData == null) {
            throw new NullPointerException("sqlMetaData must not be null");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("insert:{}", sqlMetaData);
        }

        SqlMetaDataBo sqlMetaDataBo = new SqlMetaDataBo(sqlMetaData.getAgentId(), sqlMetaData.getAgentStartTime(), sqlMetaData.getSqlId());
        final byte[] rowKey = getDistributedKey(sqlMetaDataBo.toRowKey());


        Put put = new Put(rowKey);
        String sql = sqlMetaData.getSql();
        byte[] sqlBytes = Bytes.toBytes(sql);

        put.addColumn(ElasticSearchTables.SQL_METADATA_VER2_CF_SQL, ElasticSearchTables.SQL_METADATA_VER2_CF_SQL_QUALI_SQLSTATEMENT, sqlBytes);

		this.elasticSearchTemplate.put(ElasticSearchUtils.generateIndexName(sqlMetaData.getAgentId(), ElasticSearchTables.TYPE_SQL_METADATA),
				ElasticSearchTables.TYPE_SQL_METADATA, sqlMetaDataBo.getMap());
    }

    private byte[] getDistributedKey(byte[] rowKey) {
        return rowKeyDistributorByHashPrefix.getDistributedKey(rowKey);
    }

	@Override
    public List<SqlMetaDataBo> getSqlMetaData(String agentId, long time, int sqlId) {
        if (agentId == null) {
            throw new NullPointerException("agentId must not be null");
        }

        Map<String, Object> cond = new HashMap<String, Object>();
        cond.put("agentId", agentId);
        cond.put("agentStartTime", time);
        cond.put("sqlId", sqlId);
        return elasticSearchTemplate.get(ElasticSearchUtils.generateIndexName(agentId, ElasticSearchTables.TYPE_SQL_METADATA), ElasticSearchTables.TYPE_SQL_METADATA,  cond, sqlMetaDataMapper);
        
    }

}
