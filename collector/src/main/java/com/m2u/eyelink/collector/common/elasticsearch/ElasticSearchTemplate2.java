package com.m2u.eyelink.collector.common.elasticsearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.zookeeper.Op.Delete;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.google.common.collect.Lists;
import com.m2u.eyelink.collector.util.ExecutorFactory;
import com.m2u.eyelink.collector.util.StopWatch;
import com.m2u.eyelink.util.ELAgentThreadFactory;

public class ElasticSearchTemplate2 extends ElasticSearchAccessor implements ElasticSearchOperations2, InitializingBean, DisposableBean {

    private static final int DEFAULT_MAX_THREADS_FOR_PARALLEL_SCANNER = 128;
    private static final int DEFAULT_MAX_THREADS_PER_PARALLEL_SCAN = 1;

    private static final long DEFAULT_DESTORY_TIMEOUT = 2000;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final boolean debugEnabled = this.logger.isDebugEnabled();

    private final AtomicBoolean isClose = new AtomicBoolean(false);

    private ExecutorService executor;
    private boolean enableParallelScan = false;
    private int maxThreads = DEFAULT_MAX_THREADS_FOR_PARALLEL_SCANNER;
    private int maxThreadsPerParallelScan = DEFAULT_MAX_THREADS_PER_PARALLEL_SCAN;

    private ElasticSearchAsyncOperation asyncOperation = DisabledElasticSearchAsyncOperation.INSTANCE;

    public ElasticSearchTemplate2() {
    }
    
    private Table getTable(TableName tableName) {
        return getTableFactory().getTable(tableName);
    }
    
    public void setEnableParallelScan(boolean enableParallelScan) {
        this.enableParallelScan = enableParallelScan;
    }

    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }

    public void setMaxThreadsPerParallelScan(int maxThreadsPerParallelScan) {
        this.maxThreadsPerParallelScan = maxThreadsPerParallelScan;
    }

    public void setAsyncOperation(ElasticSearchAsyncOperation asyncOperation) {
        if (asyncOperation == null) {
            throw new NullPointerException("asyncOperation");
        }
        this.asyncOperation = asyncOperation;
    }

    @Override
    public void afterPropertiesSet() {
        Configuration configuration = getConfiguration();
        Assert.notNull(configuration, "configuration is required");
        Assert.notNull(getTableFactory(), "tableFactory is required");
        ELAgentThreadFactory parallelScannerThreadFactory = new ELAgentThreadFactory("ELAgent-parallel-scanner");
        if (this.maxThreadsPerParallelScan <= 1) {
            this.enableParallelScan = false;
            this.executor = Executors.newSingleThreadExecutor(parallelScannerThreadFactory);
        } else {
            this.executor = ExecutorFactory.newFixedThreadPool(this.maxThreads, 1024, parallelScannerThreadFactory);
        }
    }

    @Override
    public void destroy() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        if (isClose.compareAndSet(false, true)) {
            logger.info("ElasticSearchTemplate2.destroy()");
            final ExecutorService executor = this.executor;
            if (executor != null) {
                executor.shutdown();
                try {
                    executor.awaitTermination(DEFAULT_DESTORY_TIMEOUT, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            long remainingTime = Math.max(DEFAULT_DESTORY_TIMEOUT - stopWatch.stop(), 100);
            awaitAsyncPutOpsCleared(remainingTime, 50);
        }
    }

    private boolean awaitAsyncPutOpsCleared(long waitTimeout, long checkUnitTime) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        while (true) {
            Long currentPutOpsCount = asyncOperation.getCurrentOpsCount();
            logger.warn("count " + currentPutOpsCount);
            if (currentPutOpsCount <= 0L) {
                return true;
            }

            if (stopWatch.stop() > waitTimeout) {
                return false;
            }
            try {
                Thread.sleep(checkUnitTime);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    private void assertAccessAvailable() {
        if (isClose.get()) {
            throw new ElasticSearchAccessException("Already closed.");
        }
    }

    @Override
    public <T> T find(TableName tableName, String family, final ResultsExtractor<T> action) {
        Scan scan = new Scan();
        scan.addFamily(family.getBytes(getCharset()));
        return find(tableName, scan, action);
    }

    @Override
    public <T> T find(TableName tableName, String family, String qualifier, final ResultsExtractor<T> action) {
        Scan scan = new Scan();
        scan.addColumn(family.getBytes(getCharset()), qualifier.getBytes(getCharset()));
        return find(tableName, scan, action);
    }

    @Override
    public <T> T find(TableName tableName, final Scan scan, final ResultsExtractor<T> action) {
        assertAccessAvailable();
        return execute(tableName, new TableCallback<T>() {
            @Override
            public T doInTable(Table table) throws Throwable {
                final ResultScanner scanner = table.getScanner(scan);
                try {
                    return action.extractData(scanner);
                } finally {
                    scanner.close();
                }
            }
        });
    }

    @Override
    public <T> List<T> find(TableName tableName, String family, final RowMapper<T> action) {
        Scan scan = new Scan();
        scan.addFamily(family.getBytes(getCharset()));
        return find(tableName, scan, action);
    }

    @Override
    public <T> List<T> find(TableName tableName, String family, String qualifier, final RowMapper<T> action) {
        Scan scan = new Scan();
        scan.addColumn(family.getBytes(getCharset()), qualifier.getBytes(getCharset()));
        return find(tableName, scan, action);
    }

    @Override
    public <T> List<T> find(TableName tableName, final Scan scan, final RowMapper<T> action) {
        return find(tableName, scan, new RowMapperResultsExtractor<>(action));
    }

    @Override
    public <T> T get(TableName tableName, String rowName, final RowMapper<T> mapper) {
        return get(tableName, rowName, null, null, mapper);
    }

    @Override
    public <T> T get(TableName tableName, String rowName, String familyName, final RowMapper<T> mapper) {
        return get(tableName, rowName, familyName, null, mapper);
    }

    @Override
    public <T> T get(TableName tableName, final String rowName, final String familyName, final String qualifier, final RowMapper<T> mapper) {
        assertAccessAvailable();
        return execute(tableName, new TableCallback<T>() {
            @Override
            public T doInTable(Table table) throws Throwable {
                Get get = new Get(rowName.getBytes(getCharset()));
                if (familyName != null) {
                    byte[] family = familyName.getBytes(getCharset());

                    if (qualifier != null) {
                        get.addColumn(family, qualifier.getBytes(getCharset()));
                    } else {
                        get.addFamily(family);
                    }
                }
                Result result = table.get(get);
                return mapper.mapRow(result, 0);
            }
        });
    }

    @Override
    public <T> T get(TableName tableName, byte[] rowName, RowMapper<T> mapper) {
        return get(tableName, rowName, null, null, mapper);
    }

    @Override
    public <T> T get(TableName tableName, byte[] rowName, byte[] familyName, RowMapper<T> mapper) {
        return get(tableName, rowName, familyName, null, mapper);
    }

    @Override
    public <T> T get(TableName tableName, final byte[] rowName, final byte[] familyName, final byte[] qualifier, final RowMapper<T> mapper) {
        assertAccessAvailable();
        return execute(tableName, new TableCallback<T>() {
            @Override
            public T doInTable(Table table) throws Throwable {
                Get get = new Get(rowName);
                if (familyName != null) {
                    if (qualifier != null) {
                        get.addColumn(familyName, qualifier);
                    } else {
                        get.addFamily(familyName);
                    }
                }
                Result result = table.get(get);
                return mapper.mapRow(result, 0);
            }
        });
    }

    @Override
    public <T> T get(TableName tableName, final Get get, final RowMapper<T> mapper) {
        assertAccessAvailable();
        return execute(tableName, new TableCallback<T>() {
            @Override
            public T doInTable(Table table) throws Throwable {
                Result result = table.get(get);
                return mapper.mapRow(result, 0);
            }
        });
    }

    @Override
    public <T> List<T> get(TableName tableName, final List<Get> getList, final RowMapper<T> mapper) {
        assertAccessAvailable();
        return execute(tableName, new TableCallback<List<T>>() {
            @Override
            public List<T> doInTable(Table table) throws Throwable {
                Result[] result = table.get(getList);
                List<T> list = new ArrayList<>(result.length);
                for (int i = 0; i < result.length; i++) {
                    T t = mapper.mapRow(result[i], i);
                    list.add(t);
                }
                return list;
            }
        });
    }

    @Override
    public void put(TableName tableName, final byte[] rowName, final byte[] familyName, final byte[] qualifier, final byte[] value) {
        put(tableName, rowName, familyName, qualifier, null, value);
    }

    @Override
    public void put(TableName tableName, final byte[] rowName, final byte[] familyName, final byte[] qualifier, final Long timestamp, final byte[] value) {
        assertAccessAvailable();
        execute(tableName, new TableCallback() {
            @Override
            public Object doInTable(Table table) throws Throwable {
                Put put = createPut(rowName, familyName, timestamp, qualifier, value);
                table.put(put);
                return null;
            }
        });
    }

    @Override
    public <T> void put(TableName tableName, final byte[] rowName, final byte[] familyName, final byte[] qualifier, final T value, final ValueMapper<T> mapper) {
        put(tableName, rowName, familyName, qualifier, null, value, mapper);
    }

    @Override
    public <T> void put(TableName tableName, final byte[] rowName, final byte[] familyName, final byte[] qualifier, final Long timestamp, final T value, final ValueMapper<T> mapper) {
        assertAccessAvailable();
        execute(tableName, new TableCallback<T>() {
            @Override
            public T doInTable(Table table) throws Throwable {
                byte[] bytes = mapper.mapValue(value);
                Put put = createPut(rowName, familyName, timestamp, qualifier, bytes);
                table.put(put);
                return null;
            }
        });
    }

    @Override
    public void put(TableName tableName, final Put put) {
        assertAccessAvailable();
        execute(tableName, new TableCallback() {
            @Override
            public Object doInTable(Table table) throws Throwable {
                table.put(put);
                return null;
            }
        });
    }

    @Override
    public void put(TableName tableName, final List<Put> puts) {
        assertAccessAvailable();
        execute(tableName, new TableCallback() {
            @Override
            public Object doInTable(Table table) throws Throwable {
                table.put(puts);
                return null;
            }
        });
    }

    @Override
    public boolean asyncPut(TableName tableName, byte[] rowName, byte[] familyName, byte[] qualifier, byte[] value) {
        return asyncPut(tableName, rowName, familyName, qualifier, null, value);
    }

    @Override
    public boolean asyncPut(TableName tableName, byte[] rowName, byte[] familyName, byte[] qualifier, Long timestamp, byte[] value) {
        Put put = createPut(rowName, familyName, timestamp, qualifier, value);
        return asyncPut(tableName, put);
    }

    @Override
    public <T> boolean asyncPut(TableName tableName, byte[] rowName, byte[] familyName, byte[] qualifier, T value, ValueMapper<T> mapper) {
        return asyncPut(tableName, rowName, familyName, qualifier, null, value, mapper);
    }

    @Override
    public <T> boolean asyncPut(TableName tableName, byte[] rowName, byte[] familyName, byte[] qualifier, Long timestamp, T value, ValueMapper<T> mapper) {
        byte[] bytes = mapper.mapValue(value);
        Put put = createPut(rowName, familyName, timestamp, qualifier, bytes);
        return asyncPut(tableName, put);
    }

    @Override
    public boolean asyncPut(TableName tableName, Put put) {
        assertAccessAvailable();
        if (asyncOperation.isAvailable()) {
            return asyncOperation.put(tableName, put);
        } else {
            put(tableName, put);
            return true;
        }
    }

    @Override
    public List<Put> asyncPut(TableName tableName, List<Put> puts) {
        assertAccessAvailable();
        if (asyncOperation.isAvailable()) {
            return asyncOperation.put(tableName, puts);
        } else {
            put(tableName, puts);
            return Collections.emptyList();
        }
    }

    private Put createPut(byte[] rowName, byte[] familyName, Long timestamp, byte[] qualifier, byte[] value) {
        Put put = new Put(rowName);
        if (familyName != null) {
            if (timestamp == null) {
                put.addColumn(familyName, qualifier, value);
            } else {
                put.addColumn(familyName, qualifier, timestamp, value);
            }
        }
        return put;
    }

    @Override
    public void delete(TableName tableName, final Delete delete) {
        assertAccessAvailable();
        execute(tableName, new TableCallback() {
            @Override
            public Object doInTable(Table table) throws Throwable {
                table.delete(delete);
                return null;
            }
        });
    }

    @Override
    public void delete(TableName tableName, final List<Delete> deletes) {
        assertAccessAvailable();
        execute(tableName, new TableCallback() {
            @Override
            public Object doInTable(Table table) throws Throwable {
                table.delete(deletes);
                return null;
            }
        });
    }

    @Override
    public <T> List<T> find(TableName tableName, final List<Scan> scanList, final ResultsExtractor<T> action) {
        assertAccessAvailable();
        return execute(tableName, new TableCallback<List<T>>() {
            @Override
            public List<T> doInTable(Table table) throws Throwable {
                List<T> result = new ArrayList<>(scanList.size());
                for (Scan scan : scanList) {
                    final ResultScanner scanner = table.getScanner(scan);
                    try {
                        T t = action.extractData(scanner);
                        result.add(t);
                    } finally {
                        scanner.close();
                    }
                }
                return result;
            }
        });
    }

    @Override
    public <T> List<List<T>> find(TableName tableName, List<Scan> scanList, RowMapper<T> action) {
        return find(tableName, scanList, new RowMapperResultsExtractor<>(action));
    }

    @Override
    public <T> List<T> findParallel(final TableName tableName, final List<Scan> scans, final ResultsExtractor<T> action) {
        assertAccessAvailable();
        if (!this.enableParallelScan || scans.size() == 1) {
            return find(tableName, scans, action);
        }
        List<T> results = new ArrayList<>(scans.size());
        List<Callable<T>> callables = new ArrayList<>(scans.size());
        for (final Scan scan : scans) {
            callables.add(new Callable<T>() {
                @Override
                public T call() throws Exception {
                    return execute(tableName, new TableCallback<T>() {
                        @Override
                        public T doInTable(Table table) throws Throwable {
                            final ResultScanner scanner = table.getScanner(scan);
                            try {
                                return action.extractData(scanner);
                            } finally {
                                scanner.close();
                            }
                        }
                    });
                }
            });
        }
        List<List<Callable<T>>> callablePartitions = Lists.partition(callables, this.maxThreadsPerParallelScan);
        for (List<Callable<T>> callablePartition : callablePartitions) {
            try {
                List<Future<T>> futures = this.executor.invokeAll(callablePartition);
                for (Future<T> future : futures) {
                    results.add(future.get());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("interrupted while findParallel [{}].", tableName);
                return Collections.emptyList();
            } catch (ExecutionException e) {
                logger.warn("findParallel [{}], error : {}", tableName, e);
                return Collections.emptyList();
            }
        }
        return results;
    }

    @Override
    public <T> List<List<T>> findParallel(TableName tableName, final List<Scan> scans, final RowMapper<T> action) {
        return findParallel(tableName, scans, new RowMapperResultsExtractor<>(action));
    }

    @Override
    public <T> List<T> find(TableName tableName, final Scan scan, final AbstractRowKeyDistributor rowKeyDistributor, final RowMapper<T> action) {
        final ResultsExtractor<List<T>> resultsExtractor = new RowMapperResultsExtractor<>(action);
        return executeDistributedScan(tableName, scan, rowKeyDistributor, resultsExtractor);
    }

    @Override
    public <T> List<T> find(TableName tableName, final Scan scan, final AbstractRowKeyDistributor rowKeyDistributor, final int limit, final RowMapper<T> action) {
        final ResultsExtractor<List<T>> resultsExtractor = new LimitRowMapperResultsExtractor<>(action, limit);
        return executeDistributedScan(tableName, scan, rowKeyDistributor, resultsExtractor);
    }

    @Override
    public <T> List<T> find(TableName tableName, final Scan scan, final AbstractRowKeyDistributor rowKeyDistributor, int limit, final RowMapper<T> action, final LimitEventHandler limitEventHandler) {
        final LimitRowMapperResultsExtractor<T> resultsExtractor = new LimitRowMapperResultsExtractor<>(action, limit, limitEventHandler);
        return executeDistributedScan(tableName, scan, rowKeyDistributor, resultsExtractor);
    }

    @Override
    public <T> T find(TableName tableName, final Scan scan, final AbstractRowKeyDistributor rowKeyDistributor, final ResultsExtractor<T> action) {
        return executeDistributedScan(tableName, scan, rowKeyDistributor, action);
    }

    protected final <T> T executeDistributedScan(TableName tableName, final Scan scan, final AbstractRowKeyDistributor rowKeyDistributor, final ResultsExtractor<T> action) {
        assertAccessAvailable();
        return execute(tableName, new TableCallback<T>() {
            @Override
            public T doInTable(Table table) throws Throwable {
                StopWatch watch = null;
                if (debugEnabled) {
                    watch = new StopWatch();
                    watch.start();
                }
                final ResultScanner[] splitScanners = splitScan(table, scan, rowKeyDistributor);
                final ResultScanner scanner = new DistributedScanner(rowKeyDistributor, splitScanners);
                if (debugEnabled) {
                    logger.debug("DistributedScanner createTime: {}ms", watch.stop());
                    watch.start();
                }
                try {
                    return action.extractData(scanner);
                } finally {
                    scanner.close();
                    if (debugEnabled) {
                        logger.debug("DistributedScanner scanTime: {}ms", watch.stop());
                    }
                }
            }
        });
    }

    private ResultScanner[] splitScan(Table table, Scan originalScan, AbstractRowKeyDistributor rowKeyDistributor) throws IOException {
        Scan[] scans = rowKeyDistributor.getDistributedScans(originalScan);
        final int length = scans.length;
        for (int i = 0; i < length; i++) {
            Scan scan = scans[i];
            // other properties are already set upon construction
            scan.setId(scan.getId() + "-" + i);
        }

        ResultScanner[] scanners = new ResultScanner[length];
        boolean success = false;
        try {
            for (int i = 0; i < length; i++) {
                scanners[i] = table.getScanner(scans[i]);
            }
            success = true;
        } finally {
            if (!success) {
                closeScanner(scanners);
            }
        }
        return scanners;
    }

    private void closeScanner(ResultScanner[] scannerList) {
        for (ResultScanner scanner : scannerList) {
            if (scanner != null) {
                try {
                    scanner.close();
                } catch (Exception e) {
                    logger.warn("Scanner.close() error Caused:{}", e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public <T> List<T> findParallel(TableName tableName, Scan scan, AbstractRowKeyDistributor rowKeyDistributor, RowMapper<T> action, int numParallelThreads) {
        if (!this.enableParallelScan || numParallelThreads <= 1) {
            // use DistributedScanner if parallel scan is disabled or if called to use a single thread
            return find(tableName, scan, rowKeyDistributor, action);
        } else {
            int numThreadsUsed = numParallelThreads < this.maxThreadsPerParallelScan ? numParallelThreads : this.maxThreadsPerParallelScan;
            final ResultsExtractor<List<T>> resultsExtractor = new RowMapperResultsExtractor<>(action);
            return executeParallelDistributedScan(tableName, scan, rowKeyDistributor, resultsExtractor, numThreadsUsed);
        }
    }

    @Override
    public <T> List<T> findParallel(TableName tableName, Scan scan, AbstractRowKeyDistributor rowKeyDistributor, int limit, RowMapper<T> action, int numParallelThreads) {
        if (!this.enableParallelScan || numParallelThreads <= 1) {
            // use DistributedScanner if parallel scan is disabled or if called to use a single thread
            return find(tableName, scan, rowKeyDistributor, limit, action);
        } else {
            int numThreadsUsed = numParallelThreads < this.maxThreadsPerParallelScan ? numParallelThreads : this.maxThreadsPerParallelScan;
            final ResultsExtractor<List<T>> resultsExtractor = new LimitRowMapperResultsExtractor<>(action, limit);
            return executeParallelDistributedScan(tableName, scan, rowKeyDistributor, resultsExtractor, numThreadsUsed);
        }
    }

    @Override
    public <T> List<T> findParallel(TableName tableName, Scan scan, AbstractRowKeyDistributor rowKeyDistributor, int limit, RowMapper<T> action, LimitEventHandler limitEventHandler, int numParallelThreads) {
        if (!this.enableParallelScan || numParallelThreads <= 1) {
            // use DistributedScanner if parallel scan is disabled or if called to use a single thread
            return find(tableName, scan, rowKeyDistributor, limit, action, limitEventHandler);
        } else {
            int numThreadsUsed = numParallelThreads < this.maxThreadsPerParallelScan ? numParallelThreads : this.maxThreadsPerParallelScan;
            final LimitRowMapperResultsExtractor<T> resultsExtractor = new LimitRowMapperResultsExtractor<>(action, limit, limitEventHandler);
            return executeParallelDistributedScan(tableName, scan, rowKeyDistributor, resultsExtractor, numThreadsUsed);
        }
    }

    @Override
    public <T> T findParallel(TableName tableName, Scan scan, AbstractRowKeyDistributor rowKeyDistributor, ResultsExtractor<T> action, int numParallelThreads) {
        if (!this.enableParallelScan || numParallelThreads <= 1) {
            // use DistributedScanner if parallel scan is disabled or if called to use a single thread
            return find(tableName, scan, rowKeyDistributor, action);
        } else {
            int numThreadsUsed = numParallelThreads < this.maxThreadsPerParallelScan ? numParallelThreads : this.maxThreadsPerParallelScan;
            return executeParallelDistributedScan(tableName, scan, rowKeyDistributor, action, numThreadsUsed);
        }
    }

    protected final <T> T executeParallelDistributedScan(TableName tableName, Scan scan, AbstractRowKeyDistributor rowKeyDistributor, ResultsExtractor<T> action, int numParallelThreads) {
        assertAccessAvailable();
        try {
            StopWatch watch = null;
            if (debugEnabled) {
                watch = new StopWatch();
                watch.start();
            }
            ParallelResultScanner scanner = new ParallelResultScanner(tableName, this, this.executor, scan, rowKeyDistributor, numParallelThreads);
            if (debugEnabled) {
                logger.debug("ParallelDistributedScanner createTime: {}ms", watch.stop());
                watch.start();
            }
            try {
                return action.extractData(scanner);
            } finally {
                scanner.close();
                if (debugEnabled) {
                    logger.debug("ParallelDistributedScanner scanTime: {}ms", watch.stop());
                }
            }
        } catch (Throwable th) {
            Throwable throwable = th;
            if (th instanceof ScanTaskException) {
                throwable = th.getCause();
            }
            if (throwable instanceof Error) {
                throw ((Error) th);
            }
            if (throwable instanceof RuntimeException) {
                throw ((RuntimeException) th);
            }
            throw new ElasticSearchSystemException((Exception) throwable);
        }
    }

    @Override
    public Result increment(TableName tableName, final Increment increment) {
        assertAccessAvailable();
        return execute(tableName, new TableCallback<Result>() {
            @Override
            public Result doInTable(Table table) throws Throwable {
                return table.increment(increment);
            }
        });
    }

    @Override
    public List<Result> increment(final TableName tableName, final List<Increment> incrementList) {
        assertAccessAvailable();
        return execute(tableName, new TableCallback<List<Result>>() {
            @Override
            public List<Result> doInTable(Table table) throws Throwable {
                final List<Result> resultList = new ArrayList<>(incrementList.size());

                Exception lastException = null;
                for (Increment increment : incrementList) {
                    try {
                        Result result = table.increment(increment);
                        resultList.add(result);
                    } catch (Exception e) {
                        logger.warn("{} increment error Caused:{}", tableName, e.getMessage(), e);
                        lastException = e;
                    }
                }
                if (lastException != null) {
                    throw lastException;
                }
                return resultList;
            }
        });
    }

    @Override
    public long incrementColumnValue(TableName tableName, final byte[] rowName, final byte[] familyName, final byte[] qualifier, final long amount) {
        assertAccessAvailable();
        return execute(tableName, new TableCallback<Long>() {
            @Override
            public Long doInTable(Table table) throws Throwable {
                return table.incrementColumnValue(rowName, familyName, qualifier, amount);
            }
        });
    }

    @Override
    public long incrementColumnValue(TableName tableName, final byte[] rowName, final byte[] familyName, final byte[] qualifier, final long amount, final boolean writeToWAL) {
        assertAccessAvailable();
        return execute(tableName, new TableCallback<Long>() {
            @Override
            public Long doInTable(Table table) throws Throwable {
                return table.incrementColumnValue(rowName, familyName, qualifier, amount, writeToWAL? Durability.SKIP_WAL: Durability.USE_DEFAULT);
            }
        });
    }
    
    @Override
    public <T> T execute(TableName tableName, TableCallback<T> action) {
        Assert.notNull(action, "Callback object must not be null");
        Assert.notNull(tableName, "No table specified");
        assertAccessAvailable();

        Table table = getTable(tableName);

        try {
            T result = action.doInTable(table);
            return result;
        } catch (Throwable e) {
            if (e instanceof Error) {
                throw ((Error) e);
            }
            if (e instanceof RuntimeException) {
                throw ((RuntimeException) e);
            }
            throw new ElasticSearchSystemException((Exception) e);
        } finally {
            releaseTable(table);
        }
    }
    
    private void releaseTable(Table table) {
        getTableFactory().releaseTable(table);
    }
    
    // For ElasticSearch 
    public void put(String IndexName, String typeName, String jsonData) {
        assertAccessAvailable();
        execute(IndexName, typeName, new ActionCallback() {
            @Override
            public Object doInsert() throws Throwable {
                insertData(IndexName, typeName, jsonData);
                return null;
            }
        });		
	}

    private boolean insertData(String indexName, String typeName, String jsonData) {
        return getTableFactory().insertData(indexName, typeName, jsonData);
    }

    public void put(String IndexName, String typeName, Map mapData) {
        assertAccessAvailable();
        execute(IndexName, typeName, new ActionCallback() {
            @Override
            public Object doInsert() throws Throwable {
                insertData(IndexName, typeName, mapData);
                return null;
            }
        });		
	}

    private boolean insertData(String indexName, String typeName, Map mapData) {
        return getTableFactory().insertData(indexName, typeName, mapData);
    }

    @Override
    public <T> T execute(String indexName, String typeName, ActionCallback<T> action) {
        Assert.notNull(action, "Callback object must not be null");
        Assert.notNull(indexName, "No indexName specified");
        Assert.notNull(typeName, "No typeName specified");
        assertAccessAvailable();

        try {
            T result = action.doInsert();
            return result;
        } catch (Throwable e) {
            if (e instanceof Error) {
                throw ((Error) e);
            }
            if (e instanceof RuntimeException) {
                throw ((RuntimeException) e);
            }
            throw new ElasticSearchSystemException((Exception) e);
        } finally {
        		// TO-DO check need or not
//            releaseTable(table);
        }
    }

}
