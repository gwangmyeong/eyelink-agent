package com.m2u.eyelink.context;

import com.navercorp.pinpoint.profiler.context.storage.Storage;

public interface StorageFactory {
    Storage createStorage();

}
