package com.m2u.eyelink.collector.dao.elasticsearch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.m2u.eyelink.collector.bo.ApiMetaDataBo;
import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchOperations2;
import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchTables;
import com.m2u.eyelink.collector.common.elasticsearch.Put;
import com.m2u.eyelink.collector.dao.ApiMetaDataDao;
import com.m2u.eyelink.collector.util.ElasticSearchUtils;
import com.m2u.eyelink.context.TApiMetaData;
import com.m2u.eyelink.util.AutomaticBuffer;
import com.m2u.eyelink.util.Buffer;

@Repository
public class ElasticSearchApiMetaDataDao implements ApiMetaDataDao {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ElasticSearchOperations2 elasticSearchTemplate;

	@Autowired
	@Qualifier("metadataRowKeyDistributor")
	private RowKeyDistributorByHashPrefix rowKeyDistributorByHashPrefix;

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
}
