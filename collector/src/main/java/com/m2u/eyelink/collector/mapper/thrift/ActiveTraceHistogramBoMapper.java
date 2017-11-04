package com.m2u.eyelink.collector.mapper.thrift;

import java.util.List;

import org.springframework.stereotype.Component;

import com.m2u.eyelink.collector.bo.ActiveTraceHistogramBo;
import com.m2u.eyelink.thrift.TActiveTraceHistogram;

@Component
public class ActiveTraceHistogramBoMapper implements ThriftBoMapper<ActiveTraceHistogramBo, TActiveTraceHistogram> {

    @Override
    public ActiveTraceHistogramBo map(TActiveTraceHistogram tActiveTraceHistogram) {
        int version = tActiveTraceHistogram.getVersion();
        int histogramSchemaType = tActiveTraceHistogram.getHistogramSchemaType();
        List<Integer> activeThreadCount = tActiveTraceHistogram.getActiveTraceCount();
        ActiveTraceHistogramBo bo = new ActiveTraceHistogramBo(version, histogramSchemaType, activeThreadCount);
        return bo;
    }

}
