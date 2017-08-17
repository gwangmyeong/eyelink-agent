package com.m2u.eyelink.collector.common.elasticsearch;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

public class ConnectionFactory {

	public static TransportClient createConnection(Configuration configuration) throws IOException {
		return createConnection(configuration, null);
	}

	public static TransportClient createConnection(Configuration config, ExecutorService executor) throws IOException {
		// connection
		Settings settings = Settings.builder().put("cluster.name", config.get("elasticsearch.cluster.name")).build();

		TransportClient connection = new PreBuiltTransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(
						InetAddress.getByName(config.get("elasticsearch.host.ip.1")), Integer.parseInt(config.get("elasticsearch.host.port.1"))))
				.addTransportAddress(new InetSocketTransportAddress(
						InetAddress.getByName(config.get("elasticsearch.host.ip.2")), Integer.parseInt(config.get("elasticsearch.host.port.2"))));
		// TO-DO Multi Domain 에 대한 처리 로직 추가 필요.
		;
		return connection;
	}

}
