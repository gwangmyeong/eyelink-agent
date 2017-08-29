package com.m2u.eyelink.collector.util;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchTables;

public class ElasticSearchUtils {
	public static String generateIndexName(String agentId) {
		Date dt = new Date();
		SimpleDateFormat dt1 = new SimpleDateFormat("yyyy.MM.dd");
		String indexName = ElasticSearchTables.INDEX_NAME_PREFIX + "_" + agentId + "-" + dt1.format(dt);
		return indexName.toLowerCase();
	}



}
