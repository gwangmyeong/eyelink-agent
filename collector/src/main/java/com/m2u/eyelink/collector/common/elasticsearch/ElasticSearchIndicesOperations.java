package com.m2u.eyelink.collector.common.elasticsearch;

import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.client.Client;

public class ElasticSearchIndicesOperations {
	private final Client client;
	
	public ElasticSearchIndicesOperations(Client client) {
		this.client = client;
	}
	
	public boolean checkIndexExists(String name) {
		IndicesExistsResponse res = client.admin().indices().prepareExists(name).execute().actionGet();
		return res.isExists();
	}
	
	public void createIndex(String name) {
		client.admin().indices().prepareCreate(name).execute().actionGet();
	}
	
	public void deleteIndex(String name) {
		client.admin().indices().prepareDelete(name).execute().actionGet();
	}
	
	public void closeIndex(String name) {
		client.admin().indices().prepareClose(name).execute().actionGet();
	}
	
	public void openIndex(String name) {
		client.admin().indices().prepareOpen(name).execute().actionGet();
	}
	
}
