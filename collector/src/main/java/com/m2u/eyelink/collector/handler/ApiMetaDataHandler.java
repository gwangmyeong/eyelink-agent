package com.m2u.eyelink.collector.handler;

import org.apache.thrift.TBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.m2u.eyelink.collector.dao.ApiMetaDataDao;
import com.m2u.eyelink.thrift.TResult;
import com.m2u.eyelink.thrift.dto.TApiMetaData;

public class ApiMetaDataHandler implements RequestResponseHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ApiMetaDataDao sqlMetaDataDao;

    @Override
    public TBase<?, ?> handleRequest(TBase<?, ?> tbase) {
        if (!(tbase instanceof TApiMetaData)) {
            logger.error("invalid tbase:{}", tbase);
            return null;
        }

        TApiMetaData apiMetaData = (TApiMetaData) tbase;

        // Because api meta data is important , logging it at info level.
        if (logger.isInfoEnabled()) {
            logger.info("Received ApiMetaData={}", apiMetaData);
        }

        try {
            sqlMetaDataDao.insert(apiMetaData);
        } catch (Exception e) {
            logger.warn("{} handler error. Caused:{}", this.getClass(), e.getMessage(), e);
            TResult result = new TResult(false);
            result.setMessage(e.getMessage());
            return result;
        }
        return new TResult(true);
    }
}
