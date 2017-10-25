package com.m2u.eyelink.trace;


public abstract class AnnotationKeyFactory {
    private static final AnnotationKeyFactory DEFAULT_FACTORY = new DefaultAnnotationKeyFactory();

    public static AnnotationKey of(int code, String name, AnnotationKeyProperty... properties) {
        return DEFAULT_FACTORY.createAnnotationKey(code, name, properties);
    }


    abstract AnnotationKey createAnnotationKey(int code, String name, AnnotationKeyProperty... properties);

}
