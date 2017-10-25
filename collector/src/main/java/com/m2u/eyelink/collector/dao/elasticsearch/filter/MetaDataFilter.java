package com.m2u.eyelink.collector.dao.elasticsearch.filter;

import org.apache.jute.Record;

import com.m2u.eyelink.collector.bo.AnnotationBo;
import com.m2u.eyelink.collector.common.elasticsearch.span.CallTreeNode;
import com.m2u.eyelink.collector.common.elasticsearch.span.SpanAlign;
import com.m2u.eyelink.collector.dao.elasticsearch.callstacks.RecordFactory;

public interface MetaDataFilter {
    
    public enum MetaData {
        API, SQL, PARAM
    }

    boolean filter(SpanAlign spanAlign, MetaData metaData);

    AnnotationBo createAnnotationBo(SpanAlign spanAlign, MetaData metaData);

    Record createRecord(CallTreeNode node, RecordFactory factory);

    void replaceAnnotationBo(SpanAlign align, MetaData param);
}
