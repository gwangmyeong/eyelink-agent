package com.m2u.eyelink.common.service;

import static org.junit.Assert.assertSame;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.m2u.eyelink.logging.CommonLoggerFactory;
import com.m2u.eyelink.logging.StdoutCommonLoggerFactory;
import com.m2u.eyelink.trace.AnnotationKey;
import com.m2u.eyelink.trace.AnnotationKeyFactory;
import com.m2u.eyelink.trace.ServiceType;
import com.m2u.eyelink.trace.ServiceTypeFactory;
import com.m2u.eyelink.trace.TraceMetadataLoader;
import com.m2u.eyelink.trace.TraceMetadataProvider;
import com.m2u.eyelink.trace.TraceMetadataSetupContext;
import com.m2u.eyelink.util.StaticFieldLookUp;

import static com.m2u.eyelink.trace.ServiceTypeProperty.*;

public class ServiceTypeInitializerTest {

    private CommonLoggerFactory loggerFactory = StdoutCommonLoggerFactory.INSTANCE;

    private static final ServiceType[] TEST_TYPES = {
        ServiceTypeFactory.of(1209, "FOR_UNIT_TEST", "UNDEFINED", TERMINAL, RECORD_STATISTICS, INCLUDE_DESTINATION_ID)
    };
    
    private static final AnnotationKey[] TEST_KEYS = {
        AnnotationKeyFactory.of(1209, "Duplicate-API")
    };

    private static final ServiceType[] DUPLICATED_CODE_WITH_DEFAULT_TYPE = {
        ServiceTypeFactory.of(ServiceType.USER.getCode(), "FOR_UNIT_TEST", "UNDEFINED", TERMINAL, RECORD_STATISTICS, INCLUDE_DESTINATION_ID)
    };
    
    private static final ServiceType[] DUPLICATED_NAME_WITH_DEFAULT_TYPE = {
        ServiceTypeFactory.of(1209, ServiceType.USER.getName(), "UNDEFINED", TERMINAL, RECORD_STATISTICS, INCLUDE_DESTINATION_ID)
    };
    
    private static final AnnotationKey[] DUPLICATED_CODE_WITH_DEFAULT_KEY = {
        AnnotationKeyFactory.of(AnnotationKey.ARGS0.getCode(), "API")
    };

    private void verifyAnnotationKeys(List<AnnotationKey> annotationKeys, AnnotationKeyRegistryService annotationKeyRegistryService) {
        for (AnnotationKey key : annotationKeys) {
            assertSame(key, annotationKeyRegistryService.findAnnotationKey(key.getCode()));
        }
    }


    @Test
    public void testWithPlugins() {

        List<TraceMetadataProvider> typeProviders = Arrays.<TraceMetadataProvider>asList(new TestProvider(TEST_TYPES, TEST_KEYS));
        TraceMetadataLoaderService typeLoaderService = new DefaultTraceMetadataLoaderService(typeProviders, loggerFactory);
        AnnotationKeyRegistryService annotationKeyRegistryService = new DefaultAnnotationKeyRegistryService(typeLoaderService, loggerFactory);

        StaticFieldLookUp<AnnotationKey> lookUp = new StaticFieldLookUp<AnnotationKey>(AnnotationKey.class, AnnotationKey.class);
        verifyAnnotationKeys(lookUp.lookup(), annotationKeyRegistryService);


        verifyAnnotationKeys(Arrays.asList(TEST_KEYS), annotationKeyRegistryService);
    }
    
    @Test(expected=RuntimeException.class)
    public void testDuplicated() {

        List<TraceMetadataProvider> providers = Arrays.<TraceMetadataProvider>asList(
                new TestProvider(TEST_TYPES, TEST_KEYS),
                new TestProvider(new ServiceType[0], TEST_KEYS)
        );

        TraceMetadataLoader loader = new TraceMetadataLoader();
        loader.load(providers);
    }
    
    @Test(expected=RuntimeException.class)
    public void testDuplicated2() {
        List<TraceMetadataProvider> providers = Arrays.<TraceMetadataProvider>asList(
                new TestProvider(TEST_TYPES, TEST_KEYS),
                new TestProvider(TEST_TYPES, new AnnotationKey[0])
        );

        TraceMetadataLoader loader = new TraceMetadataLoader();
        loader.load(providers);
    }
    
    @Test(expected=RuntimeException.class)
    public void testDuplicated3() {
        List<TraceMetadataProvider> providers = Arrays.<TraceMetadataProvider>asList(
                new TestProvider(TEST_TYPES, TEST_KEYS),
                new TestProvider(TEST_TYPES, new AnnotationKey[0])
        );

        TraceMetadataLoader loader = new TraceMetadataLoader();
        loader.load(providers);
    }

    @Test(expected=RuntimeException.class)
    public void testDuplicatedWithDefault() {
        List<TraceMetadataProvider> providers = Arrays.<TraceMetadataProvider>asList(
                new TestProvider(DUPLICATED_CODE_WITH_DEFAULT_TYPE, TEST_KEYS)
        );

        TraceMetadataLoaderService loaderService = new DefaultTraceMetadataLoaderService(providers, loggerFactory);
        ServiceTypeRegistryService serviceTypeRegistryService = new DefaultServiceTypeRegistryService(loaderService, loggerFactory);
    }

    @Test(expected=RuntimeException.class)
    public void testDuplicatedWithDefault2() {
        List<TraceMetadataProvider> providers = Arrays.<TraceMetadataProvider>asList(
                new TestProvider(DUPLICATED_NAME_WITH_DEFAULT_TYPE, TEST_KEYS)
        );

        TraceMetadataLoaderService loaderService = new DefaultTraceMetadataLoaderService(providers, loggerFactory);
        ServiceTypeRegistryService serviceTypeRegistryService = new DefaultServiceTypeRegistryService(loaderService, loggerFactory);
    }

    @Test(expected=RuntimeException.class)
    public void testDuplicatedWithDefault3() {
        List<TraceMetadataProvider> providers = Arrays.<TraceMetadataProvider>asList(
                new TestProvider(TEST_TYPES, DUPLICATED_CODE_WITH_DEFAULT_KEY)
        );

        TraceMetadataLoaderService loaderService = new DefaultTraceMetadataLoaderService(providers, loggerFactory);
        AnnotationKeyRegistryService annotationKeyRegistryService = new DefaultAnnotationKeyRegistryService(loaderService, loggerFactory);

    }
    
    
    private static class TestProvider implements TraceMetadataProvider {
        private final ServiceType[] serviceTypes;
        private final AnnotationKey[] annotationKeys;
        
        public TestProvider(ServiceType[] serviceTypes, AnnotationKey[] annotationKeys) {
            this.serviceTypes = serviceTypes;
            this.annotationKeys = annotationKeys;
        }
        
        @Override
        public void setup(TraceMetadataSetupContext context) {
            for (ServiceType type : serviceTypes) {
                context.addServiceType(type);
            }

            for (AnnotationKey key : annotationKeys) {
                context.addAnnotationKey(key);
            }
        }
    }
}
