package com.m2u.eyelink.collector.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchTables;

public class ElasticSearchUtils {
	public static String generateIndexName(String agentId) {
		Date dt = new Date();
		SimpleDateFormat dt1 = new SimpleDateFormat("YYYY.MM.DD");
		String indexName = ElasticSearchTables.IndexNamePrefix + "_" + agentId + "-" + dt1.format(dt);
		return indexName.toLowerCase();
	}
}
