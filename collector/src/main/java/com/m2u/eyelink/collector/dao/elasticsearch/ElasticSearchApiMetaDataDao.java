package com.m2u.eyelink.collector.dao.elasticsearch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import com.m2u.eyelink.collector.bo.ApiMetaDataBo;
import com.m2u.eyelink.collector.bo.SpanBo;
import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchOperations2;
import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchTables;
import com.m2u.eyelink.collector.common.elasticsearch.Put;
import com.m2u.eyelink.collector.common.elasticsearch.RowMapper;
import com.m2u.eyelink.collector.dao.ApiMetaDataDao;
import com.m2u.eyelink.collector.util.ElasticSearchUtils;
import com.m2u.eyelink.thrift.dto.TApiMetaData;
import com.m2u.eyelink.util.AutomaticBuffer;
import com.m2u.eyelink.util.Buffer;

@Repository
public class ElasticSearchApiMetaDataDao implements ApiMetaDataDao {
    static final String SPEL_KEY = "#agentId.toString() + '.' + #time.toString() + '.' + #apiId.toString()";

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ElasticSearchOperations2 elasticSearchTemplate;

	@Autowired
	@Qualifier("metadataRowKeyDistributor")
	private RowKeyDistributorByHashPrefix rowKeyDistributorByHashPrefix;

    @Autowired
    @Qualifier("apiMetaDataMapper")
    private RowMapper<List<ApiMetaDataBo>> apiMetaDataMapper;
	
	@Override
	public void insert(TApiMetaData apiMetaData) {
		if (logger.isDebugEnabled()) {
			logger.debug("insert:{}", apiMetaData);
		}

		ApiMetaDataBo apiMetaDataBo = new ApiMetaDataBo(apiMetaData.getAgentId(), apiMetaData.getAgentStartTime(),
				apiMetaData.getApiId(), apiMetaData.getApiInfo(), apiMetaData.getType());
		byte[] rowKey = getDistributedKey(apiMetaDataBo.toRowKey());

		final Put put = new Put(rowKey);

		final Buffer buffer = new AutomaticBuffer(64);
		String api = apiMetaData.getApiInfo();
		buffer.putPrefixedString(api);
		if (apiMetaData.isSetLine()) {
			buffer.putInt(apiMetaData.getLine());
		} else {
			buffer.putInt(-1);
		}
		if (apiMetaData.isSetType()) {
			buffer.putInt(apiMetaData.getType());
		} else {
			buffer.putInt(0);
		}

		final byte[] apiMetaDataBytes = buffer.getBuffer();
		put.addColumn(ElasticSearchTables.API_METADATA_CF_API, ElasticSearchTables.API_METADATA_CF_API_QUALI_SIGNATURE,
				apiMetaDataBytes);

//		elasticSearchTemplate.put(ElasticSearchTables.API_METADATA, put);
		elasticSearchTemplate.put(ElasticSearchUtils.generateIndexName(apiMetaData.getAgentId()),
				ElasticSearchTables.TYPE_API_METADATA, apiMetaDataBo.getMap());

	}

	private byte[] getDistributedKey(byte[] rowKey) {
		return rowKeyDistributorByHashPrefix.getDistributedKey(rowKey);
	}

    @Override
    @Cacheable(value="apiMetaData", key=SPEL_KEY)
    public List<ApiMetaDataBo> getApiMetaData(String agentId, long time, int apiId) {
        if (agentId == null) {
            throw new NullPointerException("agentId must not be null");
        }

        Map<String, Object> cond = new HashMap<String, Object>();
        cond.put("agentId", agentId);
        cond.put("agentStartTime", time);
        cond.put("apiId", apiId);
        return elasticSearchTemplate.get(ElasticSearchUtils.generateIndexName(agentId), ElasticSearchTables.TYPE_API_METADATA,  cond, apiMetaDataMapper);
    }

}
