package com.m2u.eyelink.collector.bo;

import java.util.Comparator;

import com.m2u.eyelink.collector.util.IntegerUtils;

public class AnnotationComparator implements Comparator<AnnotationBo> {

    public static final AnnotationComparator INSTANCE = new AnnotationComparator();

    @Override
    public int compare(AnnotationBo o1, AnnotationBo o2) {
        return IntegerUtils.compare(o1.getKey(), o2.getKey());
    }

}
