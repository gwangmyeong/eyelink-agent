package com.m2u.eyelink.common.trace;

public interface TraceMetadataSetupContext {
    void addServiceType(ServiceType serviceType);

    void addServiceType(ServiceType serviceType, AnnotationKeyMatcher primaryAnnotationKeyMatcher);

    void addAnnotationKey(AnnotationKey annotationKey);

}
