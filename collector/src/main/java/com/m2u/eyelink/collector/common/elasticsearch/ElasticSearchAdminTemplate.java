package com.m2u.eyelink.collector.common.elasticsearch;

import java.io.IOException;

import org.elasticsearch.client.transport.TransportClient;

public class ElasticSearchAdminTemplate {
	// FIXME bsh convert HBASE -> ElasticSearch
	
    private final Admin admin;
    private final TransportClient connection;

    public ElasticSearchAdminTemplate(Configuration configuration) {
        try {
            connection = ConnectionFactory.createConnection(configuration);
            admin = null;
        } catch (Exception e) {
            throw new ElasticSearchSystemException(e);
        }
    }
//
//    public boolean createTableIfNotExist(HTableDescriptor htd) {
//        try {
//            if (!admin.tableExists(htd.getTableName())) {
//                this.admin.createTable(htd);
//                return true;
//            }
//            return false;
//        } catch (IOException e) {
//            throw new ElasticSearchSystemException(e);
//        }
//    }
//
    public boolean tableExists(TableName tableName) {
        try {
        		// FIXME, change logic
//            return admin.tableExists(tableName);
            return true;
        } catch (Exception e) {
            throw new ElasticSearchSystemException(e);
        }
    }
//
//    public boolean dropTableIfExist(TableName tableName) {
//        try {
//            if (admin.tableExists(tableName)) {
//                this.admin.disableTable(tableName);
//                this.admin.deleteTable(tableName);
//                return true;
//            }
//            return false;
//        } catch (IOException e) {
//            throw new ElasticSearchSystemException(e);
//        }
//    }
//
//    public void dropTable(TableName tableName) {
//        try {
//            this.admin.disableTable(tableName);
//            this.admin.deleteTable(tableName);
//        } catch (IOException e) {
//            throw new ElasticSearchSystemException(e);
//        }
//    }
//
    public void close() {
        try {
            this.admin.close();
            this.connection.close();
        } catch (Exception e) {
            throw new ElasticSearchSystemException(e);
        }
    }
}
