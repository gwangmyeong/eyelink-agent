package com.m2u.eyelink.collector.common.elasticsearch;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ExecutorService;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.plugins.Plugin;

public class Connection extends TransportClient {

	public Connection(Settings settings, Collection<Class<? extends Plugin>> plugins) {
		super(settings, plugins);
		// TODO Auto-generated constructor stub
	}

	// FIXME need to generate Logic
	public Admin getAdmin() throws Exception {
		Admin admin = new Admin();
		return admin;
	}

	public Table getTable(TableName tableName, ExecutorService executor) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
