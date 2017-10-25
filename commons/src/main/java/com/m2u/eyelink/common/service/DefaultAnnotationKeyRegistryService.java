package com.m2u.eyelink.common.service;

import java.util.List;

import com.m2u.eyelink.common.trace.AnnotationKey;
import com.m2u.eyelink.common.trace.AnnotationKeyRegistry;
import com.m2u.eyelink.common.util.StaticFieldLookUp;
import com.m2u.eyelink.common.util.logger.CommonLogger;
import com.m2u.eyelink.common.util.logger.CommonLoggerFactory;
import com.m2u.eyelink.common.util.logger.StdoutCommonLoggerFactory;

public class DefaultAnnotationKeyRegistryService implements AnnotationKeyRegistryService {

    private final CommonLogger logger;

    private final TraceMetadataLoaderService typeLoaderService;
    private final AnnotationKeyRegistry registry;

    public DefaultAnnotationKeyRegistryService() {
        this(new DefaultTraceMetadataLoaderService(), StdoutCommonLoggerFactory.INSTANCE);
    }


    public DefaultAnnotationKeyRegistryService(TraceMetadataLoaderService typeLoaderService, CommonLoggerFactory commonLogger) {
        if (typeLoaderService == null) {
            throw new NullPointerException("typeLoaderService must not be null");
        }
        if (commonLogger == null) {
            throw new NullPointerException("commonLogger must not be null");
        }
        this.logger = commonLogger.getLogger(DefaultAnnotationKeyRegistryService.class.getName());
        this.typeLoaderService = typeLoaderService;
        this.registry = buildAnnotationKeyRegistry();
    }

    private AnnotationKeyRegistry buildAnnotationKeyRegistry() {
        AnnotationKeyRegistry.Builder builder = new AnnotationKeyRegistry.Builder();

        StaticFieldLookUp<AnnotationKey> staticFieldLookUp = new StaticFieldLookUp<AnnotationKey>(AnnotationKey.class, AnnotationKey.class);
        List<AnnotationKey> lookup = staticFieldLookUp.lookup();
        for (AnnotationKey serviceType: lookup) {
            if (logger.isInfoEnabled()) {
                logger.info("add Default AnnotationKey:" + serviceType);
            }
            builder.addAnnotationKey(serviceType);
        }

        final List<AnnotationKey> types = typeLoaderService.getAnnotationKeys();
        for (AnnotationKey type : types) {
            if (logger.isInfoEnabled()) {
                logger.info("add Plugin AnnotationKey:" + type);
            }
            builder.addAnnotationKey(type);
        }

        return builder.build();
    }


    @Override
    public AnnotationKey findAnnotationKey(int annotationCode) {
        return this.registry.findAnnotationKey(annotationCode);
    }

    @Override
    public AnnotationKey findAnnotationKeyByName(String keyName) {
        return this.registry.findAnnotationKeyByName(keyName);

    }

    @Override
    public AnnotationKey findApiErrorCode(int annotationCode) {
        return this.registry.findApiErrorCode(annotationCode);
    }
}
