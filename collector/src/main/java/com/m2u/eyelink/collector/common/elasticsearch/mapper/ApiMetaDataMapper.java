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

import com.m2u.eyelink.collector.bo.ApiMetaDataBo;
import com.m2u.eyelink.collector.bo.MethodTypeEnum;
import com.m2u.eyelink.collector.common.elasticsearch.Result;
import com.m2u.eyelink.collector.common.elasticsearch.RowMapper;
import com.m2u.eyelink.collector.dao.elasticsearch.RowKeyDistributorByHashPrefix;
import com.m2u.eyelink.util.Buffer;
import com.m2u.eyelink.util.FixedBuffer;

@Component
public class ApiMetaDataMapper implements RowMapper<List<ApiMetaDataBo>> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public List<ApiMetaDataBo> mapRow(SearchResponse sres, int rowNum) throws Exception {
		List<ApiMetaDataBo> apiMetaDataList = new ArrayList<>();
		for(SearchHit hit : sres.getHits().getHits()) {
			ApiMetaDataBo apiMetaDataBo = new ApiMetaDataBo();

			MethodTypeEnum methodTypeEnum = MethodTypeEnum.DEFAULT;
			methodTypeEnum = MethodTypeEnum.valueOf((int)hit.getSource().get("type"));
			
			apiMetaDataBo.setApiInfo((String)hit.getSource().get("apiInfo"));
			apiMetaDataBo.setLineNumber((int)hit.getSource().get("line"));
			apiMetaDataBo.setMethodTypeEnum(methodTypeEnum);
			apiMetaDataList.add(apiMetaDataBo);
			if (logger.isDebugEnabled()) {
				logger.debug("read apiAnnotation:{}", apiMetaDataBo);
			}
			
		}
		return apiMetaDataList;
	}

	@Override
	public List<ApiMetaDataBo> mapRow(Result result, int rowNum) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
