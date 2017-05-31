package com.m2u.eyelink.agent.profiler.context.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.m2u.eyelink.agent.profiler.context.SpanChunkFactory;
import com.m2u.eyelink.agent.profiler.context.module.SpanDataSender;
import com.m2u.eyelink.agent.profiler.context.storage.BufferedStorageFactory;
import com.m2u.eyelink.agent.profiler.context.storage.SpanStorageFactory;
import com.m2u.eyelink.config.ProfilerConfig;
import com.m2u.eyelink.context.StorageFactory;
import com.m2u.eyelink.sender.DataSender;

public class StorageFactoryProvider implements Provider<StorageFactory> {

    private final ProfilerConfig profilerConfig;
    private final DataSender spanDataSender;
    private final SpanChunkFactory spanChunkFactory;

    @Inject
    public StorageFactoryProvider(ProfilerConfig profilerConfig, @SpanDataSender DataSender spanDataSender, SpanChunkFactory spanChunkFactory) {
        if (profilerConfig == null) {
            throw new NullPointerException("profilerConfig must not be null");
        }
        if (spanDataSender == null) {
            throw new NullPointerException("spanDataSender must not be null");
        }
        if (spanChunkFactory == null) {
            throw new NullPointerException("spanChunkFactory must not be null");
        }

        this.profilerConfig = profilerConfig;
        this.spanDataSender = spanDataSender;
        this.spanChunkFactory = spanChunkFactory;
    }

    @Override
    public StorageFactory get() {
        if (profilerConfig.isIoBufferingEnable()) {
            int ioBufferingBufferSize = this.profilerConfig.getIoBufferingBufferSize();
            return new BufferedStorageFactory(ioBufferingBufferSize, this.spanDataSender, this.spanChunkFactory);
        } else {
            return new SpanStorageFactory(spanDataSender);
        }
    }

    @Override
    public String toString() {
        return "StorageFactoryProvider{" +
                "profilerConfig=" + profilerConfig +
                ", spanDataSender=" + spanDataSender +
                ", spanChunkFactory=" + spanChunkFactory +
                '}';
    }
}
