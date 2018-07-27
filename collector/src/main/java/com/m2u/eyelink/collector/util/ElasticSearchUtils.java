package com.m2u.eyelink.collector.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchTables;

public class ElasticSearchUtils {
	public static String generateIndexName(String agentId, String typeName) {
		Date dt = new Date();
		SimpleDateFormat dt1 = new SimpleDateFormat("yyyy.MM.dd");
//		String indexName = ElasticSearchTables.INDEX_NAME_PREFIX + "_" + agentId + "-" + dt1.format(dt);
		String indexName = String.format("%s_%s_%s-%s", ElasticSearchTables.INDEX_NAME_PREFIX, agentId, typeName, dt1.format(dt));
		return indexName.toLowerCase();
	}

	public static String generateIndexNameEFSM(String idxName) {
		Date dt = new Date();
		SimpleDateFormat dt1 = new SimpleDateFormat("yyyy.MM.dd");
		String indexName = idxName + "-" + dt1.format(dt);
		return indexName.toLowerCase();
	}

}
