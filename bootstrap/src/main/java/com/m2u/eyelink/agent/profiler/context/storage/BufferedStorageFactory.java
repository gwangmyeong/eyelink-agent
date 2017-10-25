package com.m2u.eyelink.agent.profiler.context.storage;

import com.m2u.eyelink.agent.profiler.context.SpanChunkFactory;
import com.m2u.eyelink.context.Storage;
import com.m2u.eyelink.context.StorageFactory;
import com.m2u.eyelink.sender.DataSender;

public class BufferedStorageFactory implements StorageFactory {

    private final DataSender dataSender;
    private final int ioBufferingBufferSize;
    private final SpanChunkFactory spanChunkFactory;

    public BufferedStorageFactory(int ioBufferingBufferSize, DataSender dataSender, SpanChunkFactory spanChunkFactory) {
        if (dataSender == null) {
            throw new NullPointerException("dataSender must not be null");
        }
        if (spanChunkFactory == null) {
            throw new NullPointerException("spanChunkFactory must not be null");
        }
        this.dataSender = dataSender;

        this.ioBufferingBufferSize = ioBufferingBufferSize;

        this.spanChunkFactory = spanChunkFactory;
    }


    @Override
    public Storage createStorage() {
        BufferedStorage bufferedStorage = new BufferedStorage(this.dataSender, spanChunkFactory, this.ioBufferingBufferSize);
        return bufferedStorage;
    }

    @Override
    public String toString() {
        return "BufferedStorageFactory{" +
                "dataSender=" + dataSender +
                ", ioBufferingBufferSize=" + ioBufferingBufferSize +
                ", spanChunkFactory=" + spanChunkFactory +
                '}';
    }
}
