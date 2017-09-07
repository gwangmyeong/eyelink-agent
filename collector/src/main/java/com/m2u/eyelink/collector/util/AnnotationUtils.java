package com.m2u.eyelink.collector.util;

import java.util.List;

import com.m2u.eyelink.collector.bo.AnnotationBo;
import com.m2u.eyelink.trace.AnnotationKey;
import com.m2u.eyelink.util.CollectionUtils;

public final class AnnotationUtils {
    private AnnotationUtils() {
    }

    public static String findApiAnnotation(List<AnnotationBo> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        AnnotationBo annotationBo = findAnnotationBo(list, AnnotationKey.API);
        if (annotationBo != null) {
            return (String) annotationBo.getValue();
        }
        return null;
    }
    
    public static String findApiTagAnnotation(List<AnnotationBo> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        AnnotationBo annotationBo = findAnnotationBo(list, AnnotationKey.API_TAG);
        if (annotationBo != null) {
            return (String) annotationBo.getValue();
        }
        return null;
    }
    

    public static AnnotationBo findAnnotationBo(List<AnnotationBo> annotationBoList, AnnotationKey annotationKey) {
        for (AnnotationBo annotation : annotationBoList) {
            int key = annotation.getKey();
            if (annotationKey.getCode() == key) {
                return annotation;
            }
        }
        return null;
    }

}
