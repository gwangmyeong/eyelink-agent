package com.m2u.eyelink.common.service;

import java.util.List;

import com.m2u.eyelink.common.trace.AnnotationKey;
import com.m2u.eyelink.common.trace.ServiceTypeInfo;


public interface TraceMetadataLoaderService {
    List<ServiceTypeInfo> getServiceTypeInfos();

    List<AnnotationKey> getAnnotationKeys();
}
