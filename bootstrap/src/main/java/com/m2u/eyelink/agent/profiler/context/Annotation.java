package com.m2u.eyelink.agent.profiler.context;

import com.m2u.eyelink.context.AnnotationValueMapper;
import com.m2u.eyelink.context.TIntStringStringValue;
import com.m2u.eyelink.context.TIntStringValue;
import com.m2u.eyelink.thrift.TAnnotation;
import com.m2u.eyelink.thrift.dto.TAnnotationValue;

public class Annotation extends TAnnotation {

    public Annotation(int key) {
        super(key);
    }

    public Annotation(int key, Object value) {
        super(key);
        AnnotationValueMapper.mappingValue(this, value);
    }

    public Annotation(int key, TIntStringValue value) {
        super(key);
        this.setValue(TAnnotationValue.intStringValue(value));
    }

    public Annotation(int key, TIntStringStringValue value) {
        super(key);
        this.setValue(TAnnotationValue.intStringStringValue(value));
    }

    public Annotation(int key, String value) {
        super(key);
        this.setValue(TAnnotationValue.stringValue(value));
    }

    public Annotation(int key, int value) {
        super(key);
        this.setValue(TAnnotationValue.intValue(value));
    }

    public int getAnnotationKey() {
        return this.getKey();
    }

}
