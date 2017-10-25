package com.m2u.eyelink.collector.bo.serializer.trace;

public class SpanEncodingContext<T> {
    private T value;

//    private AnnotationBo prevAnnotationBo;

    public SpanEncodingContext(T value) {
        this.value = value;
    }


    public T getValue() {
        return value;
    }

//    public AnnotationBo getPrevFirstAnnotationBo() {
//        return prevAnnotationBo;
//    }
//
//    public void setPrevFirstAnnotationBo(AnnotationBo prevAnnotationBo) {
//        this.prevAnnotationBo = prevAnnotationBo;
//    }
}
