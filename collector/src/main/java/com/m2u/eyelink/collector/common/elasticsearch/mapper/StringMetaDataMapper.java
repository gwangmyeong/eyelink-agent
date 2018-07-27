package com.m2u.eyelink.collector.common.elasticsearch.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.Table.Cell;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.m2u.eyelink.agent.profiler.metadata.Result;
import com.m2u.eyelink.collector.bo.ApiMetaDataBo;
import com.m2u.eyelink.collector.bo.MethodTypeEnum;
import com.m2u.eyelink.collector.bo.StringMetaDataBo;
import com.m2u.eyelink.collector.common.elasticsearch.RowMapper;
import com.m2u.eyelink.collector.dao.elasticsearch.Bytes;
import com.m2u.eyelink.collector.dao.elasticsearch.RowKeyDistributorByHashPrefix;

@Component
public class StringMetaDataMapper implements RowMapper<List<StringMetaDataBo>> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
    @Autowired
    @Qualifier("metadataRowKeyDistributor")
    private RowKeyDistributorByHashPrefix rowKeyDistributorByHashPrefix;
    
	@Override
	public List<StringMetaDataBo> mapRow(SearchResponse sres, int rowNum) throws Exception {
		List<StringMetaDataBo> stringMetaDataList = new ArrayList<>();
		for(SearchHit hit : sres.getHits().getHits()) {
			StringMetaDataBo sqlMetaDataBo = new StringMetaDataBo();

			sqlMetaDataBo.setStringValue((String)hit.getSourceAsMap().get("stringValue"));
			stringMetaDataList.add(sqlMetaDataBo);
			if (logger.isDebugEnabled()) {
				logger.debug("read apiAnnotation:{}", sqlMetaDataBo);
			}
			
		}
		return stringMetaDataList;
	}
    private byte[] getOriginalKey(byte[] rowKey) {
        return rowKeyDistributorByHashPrefix.getOriginalKey(rowKey);
    }
	@Override
	public List<StringMetaDataBo> mapRow(com.m2u.eyelink.collector.common.elasticsearch.Result result, int rowNum)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
