package com.m2u.eyelink.collector.handler;

import org.apache.thrift.TBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.m2u.eyelink.collector.dao.StringMetaDataDao;
import com.m2u.eyelink.context.TStringMetaData;
import com.m2u.eyelink.context.thrift.TResult;

@Service
public class StringMetaDataHandler implements RequestResponseHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private StringMetaDataDao stringMetaDataDao;

    @Override
    public TBase<?, ?> handleRequest(TBase<?, ?> tbase) {
        if (!(tbase instanceof TStringMetaData)) {
            logger.error("invalid tbase:{}", tbase);
            return null;
        }

        TStringMetaData stringMetaData = (TStringMetaData) tbase;
        // because api data is important, logging it at info level
        if (logger.isInfoEnabled()) {
            logger.info("Received StringMetaData={}", stringMetaData);
        }

        try {
            stringMetaDataDao.insert(stringMetaData);
        } catch (Exception e) {
            logger.warn("{} handler error. Caused:{}", this.getClass(), e.getMessage(), e);
            TResult result = new TResult(false);
            result.setMessage(e.getMessage());
            return result;
        }
        return new TResult(true);
    }
}
