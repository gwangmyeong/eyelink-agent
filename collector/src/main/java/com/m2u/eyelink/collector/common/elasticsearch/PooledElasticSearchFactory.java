package com.m2u.eyelink.collector.common.elasticsearch;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

import com.m2u.eyelink.collector.util.ExecutorFactory;

public class PooledElasticSearchFactory implements TableFactory, DisposableBean {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final int DEFAULT_POOL_SIZE = 256;
    public static final int DEFAULT_WORKER_QUEUE_SIZE = 1024*5;
    public static final boolean DEFAULT_PRESTART_THREAD_POOL = false;

    private final ExecutorService executor;
    private final TransportClient connection;


    public PooledElasticSearchFactory(Configuration config) {
        this(config, DEFAULT_POOL_SIZE, DEFAULT_WORKER_QUEUE_SIZE, DEFAULT_PRESTART_THREAD_POOL);
    }

    public PooledElasticSearchFactory(Configuration config, int poolSize, int workerQueueSize, boolean prestartThreadPool) {
        this.executor = createExecutorService(poolSize, workerQueueSize, prestartThreadPool);
        try {
            this.connection = ConnectionFactory.createConnection(config, executor);
        } catch (IOException e) {
            throw new ElasticSearchSystemException(e);
        } 
    }

    public TransportClient getConnection() {
        return connection;
    }

    private ExecutorService createExecutorService(int poolSize, int workQueueMaxSize, boolean prestartThreadPool) {

        logger.info("create HConnectionThreadPoolExecutor poolSize:{}, workerQueueMaxSize:{}", poolSize, workQueueMaxSize);

        ThreadPoolExecutor threadPoolExecutor = ExecutorFactory.newFixedThreadPool(poolSize, workQueueMaxSize, "ELAgent-HConnectionExecutor", true);
        if (prestartThreadPool) {
            logger.info("prestartAllCoreThreads");
            threadPoolExecutor.prestartAllCoreThreads();
        }

        return threadPoolExecutor;
    }


    @Override
    public Table getTable(TableName tableName) {
        try {
            //return connection.getTable(tableName, executor);
        		return null;
        } catch (Exception e) {
            throw new ElasticSearchSystemException(e);
        }
    }

    @Override
    public void releaseTable(Table table) {
        if (table == null) {
            return;
        }

        try {
            table.close();
        } catch (IOException ex) {
            throw new ElasticSearchSystemException(ex);
        }
    }


    @Override
    public void destroy() throws Exception {
        logger.info("PooledHTableFactory.destroy()");
        
        if (connection != null) {
            try {
                this.connection.close();
            } catch (Exception ex) {
                logger.warn("Connection.close() error:" + ex.getMessage(), ex);
            }
        }

        if (this.executor != null) {
            this.executor.shutdown();
            try {
                final boolean shutdown = executor.awaitTermination(1000 * 5, TimeUnit.MILLISECONDS);
                if (!shutdown) {
                    final List<Runnable> discardTask = this.executor.shutdownNow();
                    logger.warn("discard task size:{}", discardTask.size());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // For ElasticSearch
	@Override
	public boolean insertData(String indexName, String typeName, String json) {
		IndexResponse response = connection.prepareIndex(indexName, typeName)
		        .setSource(json)
		        .get();
		if(logger.isDebugEnabled()) 
			logger.debug("indexName : {}, typeName : {}, data : {}", indexName, typeName, json);
		// FIXME 결과처리 로직 보완 필요.
		return true;
	}

	@Override
	public boolean insertData(String indexName, String typeName, Map<String, Object> map) {
		IndexResponse response = connection.prepareIndex(indexName, typeName)
		        .setSource(map)
		        .get();
		if(logger.isDebugEnabled()) 
			logger.debug("indexName : {}, typeName : {}, data : {}", indexName, typeName, map.toString());
		// FIXME 결과처리 로직 보완 필요.
		return true;

	}
    
	@Override
	public boolean insertBulkData(String indexName, String typeName, List<Map<String,Object>> list ){
		boolean isSuccess = true;
	    BulkRequestBuilder bulkRequest = connection.prepareBulk();

	    Iterator<Map<String,Object>> itr = list.iterator();

	    if (itr.hasNext()){
	        Map<String,Object> document = itr.next();
	        bulkRequest.add(connection.prepareIndex(indexName, typeName)
	                .setSource(document));
	    }

	    BulkResponse bulkResponse = bulkRequest.execute().actionGet();

		if(logger.isDebugEnabled()) 
			logger.debug("indexName : {}, typeName : {}, data : {}", indexName, typeName, list.toString());

	    if (bulkResponse.hasFailures()) {
	        System.out.println(bulkResponse.buildFailureMessage());
	        return !isSuccess;
	    }   
	    return isSuccess;
	}
    
}
