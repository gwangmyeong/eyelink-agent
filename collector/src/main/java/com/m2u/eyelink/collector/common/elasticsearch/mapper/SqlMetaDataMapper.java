package com.m2u.eyelink.collector.common.elasticsearch.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.Table.Cell;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.agent.profiler.metadata.Result;
import com.m2u.eyelink.collector.bo.ApiMetaDataBo;
import com.m2u.eyelink.collector.bo.MethodTypeEnum;
import com.m2u.eyelink.collector.bo.SqlMetaDataBo;
import com.m2u.eyelink.collector.common.elasticsearch.RowMapper;
import com.m2u.eyelink.collector.dao.elasticsearch.Bytes;
import com.m2u.eyelink.collector.dao.elasticsearch.RowKeyDistributorByHashPrefix;

//@Component
public class SqlMetaDataMapper implements RowMapper<List<SqlMetaDataBo>> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	// @Autowired
	// @Qualifier("metadataRowKeyDistributor")
	private RowKeyDistributorByHashPrefix rowKeyDistributorByHashPrefix;

	@Override
	public List<SqlMetaDataBo> mapRow(SearchResponse sres, int rowNum) throws Exception {
		List<SqlMetaDataBo> sqlMetaDataList = new ArrayList<>();
		for (SearchHit hit : sres.getHits().getHits()) {
			SqlMetaDataBo sqlMetaDataBo = new SqlMetaDataBo();

			MethodTypeEnum methodTypeEnum = MethodTypeEnum.DEFAULT;
			methodTypeEnum = MethodTypeEnum.valueOf(Integer.parseInt((String) hit.getSource().get("type")));

			sqlMetaDataBo.setSql((String) hit.getSource().get("sql"));
			sqlMetaDataList.add(sqlMetaDataBo);
			if (logger.isDebugEnabled()) {
				logger.debug("read apiAnnotation:{}", sqlMetaDataList);
			}

		}
		return sqlMetaDataList;
	}

	private byte[] getOriginalKey(byte[] rowKey) {
		return rowKeyDistributorByHashPrefix.getOriginalKey(rowKey);
	}

	public void setRowKeyDistributorByHashPrefix(RowKeyDistributorByHashPrefix rowKeyDistributorByHashPrefix) {
		this.rowKeyDistributorByHashPrefix = rowKeyDistributorByHashPrefix;
	}

	@Override
	public List<SqlMetaDataBo> mapRow(com.m2u.eyelink.collector.common.elasticsearch.Result result, int rowNum)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
