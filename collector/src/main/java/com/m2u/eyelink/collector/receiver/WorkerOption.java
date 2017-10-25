package com.m2u.eyelink.collector.receiver;

public final class WorkerOption {

    private final int workerThreadSize;
    private final int workerThreadQueueSize;

    private final boolean enableCollectMetric;

    public WorkerOption(int workerThreadSize, int workerThreadQueueSize) {
        this(workerThreadSize, workerThreadQueueSize, false);
    }

    public WorkerOption(int workerThreadSize, int workerThreadQueueSize, boolean enableCollectMetric) {
        if (workerThreadSize <= 0) {
            throw new IllegalArgumentException("workerThreadSize must be greater than 0");
        }

        if (workerThreadQueueSize <= 0) {
            throw new IllegalArgumentException("workerThreadQueueSize must be greater than 0");
        }

        this.workerThreadSize = workerThreadSize;
        this.workerThreadQueueSize = workerThreadQueueSize;
        this.enableCollectMetric = enableCollectMetric;
    }

    public int getWorkerThreadSize() {
        return workerThreadSize;
    }

    public int getWorkerThreadQueueSize() {
        return workerThreadQueueSize;
    }

    public boolean isEnableCollectMetric() {
        return enableCollectMetric;
    }

    @Override
    public String toString() {
        return "WorkerOption{" +
                "workerThreadSize=" + workerThreadSize +
                ", workerThreadQueueSize=" + workerThreadQueueSize +
                ", enableCollectMetric=" + enableCollectMetric +
                '}';
    }

}
