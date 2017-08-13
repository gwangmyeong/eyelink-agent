package com.m2u.eyelink.collector.common.elasticsearch;

import java.util.List;

import org.apache.zookeeper.Op.Delete;
import org.springframework.jdbc.core.RowMapper;

public interface ElasticSearchOperations2 {
    /**
     * Gets an individual row from the given table. The content is mapped by the given action.
     *
     * @param tableName target table
     * @param rowName   row name
     * @param mapper    row mapper
     * @return object mapping the target row
     */
    <T> T get(TableName tableName, byte[] rowName, final RowMapper<T> mapper);

    /**
     * Gets an individual row from the given table. The content is mapped by the given action.
     *
     * @param tableName  target table
     * @param rowName    row name
     * @param familyName column family
     * @param mapper     row mapper
     * @return object mapping the target row
     */
    <T> T get(TableName tableName, byte[] rowName, byte[] familyName, final RowMapper<T> mapper);

    /**
     * Gets an individual row from the given table. The content is mapped by the given action.
     *
     * @param tableName  target table
     * @param rowName    row name
     * @param familyName family
     * @param qualifier  column qualifier
     * @param mapper     row mapper
     * @return object mapping the target row
     */
    <T> T get(TableName tableName, final byte[] rowName, final byte[] familyName, final byte[] qualifier, final RowMapper<T> mapper);
    <T> T get(TableName tableName, final Get get, final RowMapper<T> mapper);
    <T> List<T> get(TableName tableName, final List<Get> get, final RowMapper<T> mapper);

    void put(TableName tableName, final byte[] rowName, final byte[] familyName, final byte[] qualifier, final byte[] value);
    void put(TableName tableName, final byte[] rowName, final byte[] familyName, final byte[] qualifier, final Long timestamp, final byte[] value);
    <T> void put(TableName tableName, final byte[] rowName, final byte[] familyName, final byte[] qualifier, final T value, final ValueMapper<T> mapper);
    <T> void put(TableName tableName, final byte[] rowName, final byte[] familyName, final byte[] qualifier, final Long timestamp, final T value, final ValueMapper<T> mapper);
    void put(TableName tableName, final Put put);
    void put(TableName tableName, final List<Put> puts);

    /**
     * If asyncOperation is not set, then execute put method instead of asyncPut method.
     */
    boolean asyncPut(TableName tableName, final byte[] rowName, final byte[] familyName, final byte[] qualifier, final byte[] value);
    /**
     * If asyncOperation is not set, then execute put method instead of asyncPut method.
     */
    boolean asyncPut(TableName tableName, final byte[] rowName, final byte[] familyName, final byte[] qualifier, final Long timestamp, final byte[] value);
    /**
     * If asyncOperation is not set, then execute put method instead of asyncPut method.
     */
    <T> boolean asyncPut(TableName tableName, final byte[] rowName, final byte[] familyName, final byte[] qualifier, final T value, final ValueMapper<T> mapper);
    /**
     * If asyncOperation is not set, then execute put method instead of asyncPut method.
     */
    <T> boolean asyncPut(TableName tableName, final byte[] rowName, final byte[] familyName, final byte[] qualifier, final Long timestamp, final T value, final ValueMapper<T> mapper);
    /**
     * If asyncOperation is not set, then execute put method instead of asyncPut method.
     */
    boolean asyncPut(TableName tableName, final Put put);

    /**
     * If asyncOperation is not set, then execute put method instead of asyncPut method.
     * @param tableName
     * @param puts
     * @return the list of puts which could not be queued or put serially. May be null if all put operations were successfully queued.
     */
    List<Put> asyncPut(TableName tableName, final List<Put> puts);

    void delete(TableName tableName, final Delete delete);
    void delete(TableName tableName, final List<Delete> deletes);

    <T> List<T> find(TableName tableName, final List<Scan> scans, final ResultsExtractor<T> action);
    <T> List<List<T>> find(TableName tableName, final List<Scan> scans, final RowMapper<T> action);

    <T> List<T> findParallel(TableName tableName, final List<Scan> scans, final ResultsExtractor<T> action);
    <T> List<List<T>> findParallel(TableName tableName, final List<Scan> scans, final RowMapper<T> action);

    // Distributed scanners
    <T> List<T> find(TableName tableName, final Scan scan, final AbstractRowKeyDistributor rowKeyDistributor, final RowMapper<T> action);
    <T> List<T> find(TableName tableName, final Scan scan, final AbstractRowKeyDistributor rowKeyDistributor, int limit, final RowMapper<T> action);
    <T> List<T> find(TableName tableName, final Scan scan, final AbstractRowKeyDistributor rowKeyDistributor, int limit, final RowMapper<T> action, final LimitEventHandler limitEventHandler);
    <T> T find(TableName tableName, final Scan scan, final AbstractRowKeyDistributor rowKeyDistributor, final ResultsExtractor<T> action);

    // Parallel scanners for distributed scans
    <T> List<T> findParallel(TableName tableName, final Scan scan, final AbstractRowKeyDistributor rowKeyDistributor, final RowMapper<T> action, int numParallelThreads);
    <T> List<T> findParallel(TableName tableName, final Scan scan, final AbstractRowKeyDistributor rowKeyDistributor, int limit, final RowMapper<T> action, int numParallelThreads);
    <T> List<T> findParallel(TableName tableName, final Scan scan, final AbstractRowKeyDistributor rowKeyDistributor, int limit, final RowMapper<T> action, final LimitEventHandler limitEventHandler, int numParallelThreads);
    <T> T findParallel(TableName tableName, final Scan scan, final AbstractRowKeyDistributor rowKeyDistributor, final ResultsExtractor<T> action, int numParallelThreads);

    Result increment(TableName tableName, final Increment increment);

    /**
     * Exception throwing can partially happen in case of incrementList. you will be accepted the last Exception.
     * If you want to retry something with catching a specific exception,
     * There is a limitation that you can't know which exception throws due to throwing always last exception.
     *
     * @param tableName
     * @param incrementList
     * @return
     */
    List<Result> increment(TableName tableName, final List<Increment> incrementList);

    long incrementColumnValue(TableName tableName, final byte[] rowName, final byte[] familyName, final byte[] qualifier, final long amount);
    long incrementColumnValue(TableName tableName, final byte[] rowName, final byte[] familyName, final byte[] qualifier, final long amount, final boolean writeToWAL);

    /**
     * Executes the given action against the specified table handling resource management.
     * <p>
     * Application exceptions thrown by the action object get propagated to the caller (can only be unchecked). 
     * Allows for returning a result object (typically a domain object or collection of domain objects).
     * 
     * @param tableName the target table
     * @param action callback object that specifies the action
     * @param <T> action type
     * @return the result object of the callback action, or null
     */
    <T> T execute(TableName tableName, TableCallback<T> action);

    /**
     * Scans the target table, using the given family. The content is processed by the given action typically
     * returning a domain object or collection of domain objects.
     * 
     * @param tableName target table
     * @param family column family
     * @param action action handling the scanner results
     * @param <T> action type
     * @return the result object of the callback action, or null
     */
    <T> T find(TableName tableName, String family, final ResultsExtractor<T> action);

    /**
     * Scans the target table, using the given column family and qualifier. 
     * The content is processed by the given action typically returning a domain object or collection of domain objects.
     * 
     * @param tableName target table
     * @param family column family
     * @param qualifier column qualifier
     * @param action action handling the scanner results
     * @param <T> action type
     * @return the result object of the callback action, or null
     */
    <T> T find(TableName tableName, String family, String qualifier, final ResultsExtractor<T> action);

    /**
     * Scans the target table using the given {@link Scan} object. Suitable for maximum control over the scanning
     * process.
     * The content is processed by the given action typically returning a domain object or collection of domain objects.
     * 
     * @param tableName target table
     * @param scan table scanner
     * @param action action handling the scanner results
     * @param <T> action type
     * @return the result object of the callback action, or null
     */
    <T> T find(TableName tableName, final Scan scan, final ResultsExtractor<T> action);

    /**
     * Scans the target table, using the given column family. 
     * The content is processed row by row by the given action, returning a list of domain objects.
     * 
     * @param tableName target table
     * @param family column family
     * @param action row mapper handling the scanner results
     * @param <T> action type
     * @return a list of objects mapping the scanned rows
     */
    <T> List<T> find(TableName tableName, String family, final RowMapper<T> action);

    /**
     * Scans the target table, using the given column family. 
     * The content is processed row by row by the given action, returning a list of domain objects.
     * 
     * @param tableName target table
     * @param family column family
     * @param qualifier column qualifier
     * @param action row mapper handling the scanner results
     * @param <T> action type
     * @return a list of objects mapping the scanned rows
     */
    <T> List<T> find(TableName tableName, String family, String qualifier, final RowMapper<T> action);

    /**
     * Scans the target table using the given {@link Scan} object. Suitable for maximum control over the scanning
     * process.
     * The content is processed row by row by the given action, returning a list of domain objects.
     * 
     * @param tableName target table
     * @param scan table scanner
     * @param action row mapper handling the scanner results
     * @param <T> action type
     * @return a list of objects mapping the scanned rows
     */
    <T> List<T> find(TableName tableName, final Scan scan, final RowMapper<T> action);

    /**
     * Gets an individual row from the given table. The content is mapped by the given action.
     * 
     * @param tableName target table
     * @param rowName row name
     * @param mapper row mapper
     * @param <T> mapper type
     * @return object mapping the target row
     */
    <T> T get(TableName tableName, String rowName, final RowMapper<T> mapper);

    /**
     * Gets an individual row from the given table. The content is mapped by the given action.
     * 
     * @param tableName target table
     * @param rowName row name
     * @param familyName column family
     * @param mapper row mapper
     * @param <T> mapper type
     * @return object mapping the target row
     */
    <T> T get(TableName tableName, String rowName, String familyName, final RowMapper<T> mapper);

    /**
     * Gets an individual row from the given table. The content is mapped by the given action.
     * 
     * @param tableName target table
     * @param rowName row name
     * @param familyName family 
     * @param qualifier column qualifier
     * @param mapper row mapper
     * @param <T> mapper type
     * @return object mapping the target row
     */
    <T> T get(TableName tableName, final String rowName, final String familyName, final String qualifier, final RowMapper<T> mapper);
}
