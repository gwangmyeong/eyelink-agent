package com.m2u.eyelink.collector.common.elasticsearch;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Configuration {
	private static Map<String, String> cfg = new HashMap<String, String>();

	public boolean getBoolean(String enableAsyncMethod, boolean defaultEnableAsyncMethod) {
		// TODO Auto-generated method stub
		return false;
	}

	public int getInt(String asyncInQueueSize, int defaultAsyncInQueueSize) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Object get(String asyncPeriodicFlushTime, Object object) {
		// TODO Auto-generated method stub
		return null;
	}

	public String get(String key) {
		return cfg.get(key);
	}

	public void setInt(String asyncPeriodicFlushTime, int defaultAsyncPeriodicFlushTime) {
		// TODO Auto-generated method stub

	}

	public void set(String key, String property) {
		cfg.put(key, property);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		Set<String> keySet = cfg.keySet();
		Iterator<String> iterator = keySet.iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			Object value = cfg.get(key);
			sb.append(key).append("=").append(value).append("\r\n");
		}
		return sb.toString(); 

	}
}
