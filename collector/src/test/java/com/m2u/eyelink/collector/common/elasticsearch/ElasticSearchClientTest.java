package com.m2u.eyelink.collector.common.elasticsearch;

import static org.junit.Assert.assertEquals;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class ElasticSearchClientTest {

	private static TransportClient client = null;
	private static String indexName = "twitter";

	@BeforeClass
	public static void connection() {
		try {
			// connection
			Settings settings = Settings.builder().put("cluster.name", "eyelink-cluster").build();

			client = new PreBuiltTransportClient(settings)
					.addTransportAddress(new InetSocketTransportAddress(
							InetAddress.getByName("m2utech.eastus.cloudapp.azure.com"), 9300))
					.addTransportAddress(new InetSocketTransportAddress(
							InetAddress.getByName("m2u-da.eastus.cloudapp.azure.com"), 9300));
			;

			// create index no mapping
			client.admin().indices().prepareCreate(indexName).get();

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void createIndex() {
		// create index mapping
		client.admin().indices().prepareCreate("twitter100")
				.setSettings(Settings.builder().put("index.number_of_shards", 3).put("index.number_of_replicas", 2))
				.get();
				
	}

	@SuppressWarnings("deprecation")
	@Test
	public void mappingIndex() {
		String typeName = "user";
		// create index no mapping
		client.admin().indices().prepareCreate(indexName+"-mapping").get();
		
		
		// add mapping
		client.admin().indices().preparePutMapping(indexName+"-mapping")   
        .setType(typeName)
        .setSource("{\n" +                              
                "  \"properties\": {\n" +
                "    \"name\": {\n" +
                "      \"type\": \"string\"\n" +
                "    }\n" +
                "  }\n" +
                "}")
        .get();
	
//		Map<String, String> mapper = new HashMap<String, String>();
//		mapper.put("properties", "");
//		mapper.put("name", "");
//		mapper.put("type", "string");
//		// add mapping
//		client.admin().indices().preparePutMapping(indexName)   
//        .setType(typeName + "100")
//        .setSource(mapper)
//        .get();

	}
	
	@Test
	public void insertDataJson() {
		String json = "{" +
		        "\"user\":\"kimchy\"," +
		        "\"postDate\":\"2013-01-30\"," +
		        "\"message\":\"trying out Elasticsearch\"" +
		    "}";
		IndexResponse response = client.prepareIndex(indexName+"-insertdata", "tweet")
		        .setSource(json)
		        .get();
		
	}
	
	@Test 
	public void insertDataMap() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("user","kimchy2");
		json.put("postDate",new Date());
		json.put("message","trying out Elasticsearch");
		
		IndexResponse response = client.prepareIndex(indexName+"-insertdata", "tweet")
		        .setSource(json)
		        .get();
	}
	
	@Test
	public void insertDataHelper() {
//		XContentBuilder builder = jsonBuilder()
//			    .startObject()
//			        .field("user", "kimchy")
//			        .field("postDate", new Date())
//			        .field("message", "trying out Elasticsearch")
//			    .endObject();
//		String json = builder.string();
	}
	
	
	@Test
	public void insertData() {
//		import com.fasterxml.jackson.databind.*;
//
//		// instance a json mapper
//		ObjectMapper mapper = new ObjectMapper(); // create once, reuse
//
//		// generate json
//		byte[] json = mapper.writeValueAsBytes(yourbeaninstance);
	}
	

	@Test
	public void getDataById() {
		GetResponse response = client.prepareGet("transactionlist-2017-06", "transactionList", "1").get();
		System.out.println(response.toString());
		assertEquals(response.getIndex(), "transactionlist-2017-06");
	}

	@Test
	@Ignore
	public void getDataByMultiId() {
		MultiGetResponse multiGetItemResponses = client.prepareMultiGet()
			    .add("twitter", "tweet", "1")           
			    .add("twitter", "tweet", "2", "3", "4") 
			    .add("another", "type", "foo")          
			    .get();

		for (MultiGetItemResponse itemResponse : multiGetItemResponses) { 
		    GetResponse response = itemResponse.getResponse();
		    if (response.isExists()) {                      
		        String json = response.getSourceAsString(); 
		        System.out.println(json);
		    }
		}
	}
	
	@Test
	public void getDataUsingSearch() {
		SearchResponse response = client.prepareSearch("twitter", "twitter-insertdata")
		        .setTypes("type1", "type2")
		        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
		        .setQuery(QueryBuilders.termQuery("multi", "test"))                 // Query
		        .setPostFilter(QueryBuilders.rangeQuery("age").from(12).to(18))     // Filter
		        .setFrom(0).setSize(60).setExplain(true)
		        .get();
		System.out.println(response.toString());
	}

	@AfterClass
	public static void close() {
		deleteIndex();
		
		// close
		client.close();
	}
	
	public static void deleteIndex() {
		// delete index
		IndicesAdminClient adminClient = client.admin().indices();
		DeleteIndexResponse delete = adminClient.delete(new DeleteIndexRequest(indexName + "*")).actionGet();
		if (!delete.isAcknowledged()) {
		    System.out.println("Index " + indexName + " wasn't deleted");
		}

	}
}