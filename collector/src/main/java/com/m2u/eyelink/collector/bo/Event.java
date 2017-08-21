package com.m2u.eyelink.collector.bo;

import java.util.List;

public interface Event {

    short getServiceType();

    List<AnnotationBo> getAnnotationBoList();
}
