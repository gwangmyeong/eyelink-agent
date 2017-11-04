package com.m2u.eyelink.collector.handler;

import org.apache.thrift.TBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.collector.dao.SqlMetaDataDao;
import com.m2u.eyelink.thrift.TResult;
import com.m2u.eyelink.thrift.dto.TSqlMetaData;

public class SqlMetaDataHandler implements RequestResponseHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());

//    @Autowired
    private SqlMetaDataDao sqlMetaDataDao;

    @Override
    public TBase<?, ?> handleRequest(TBase<?, ?> tbase) {
        if (!(tbase instanceof TSqlMetaData)) {
            logger.error("invalid tbase:{}", tbase);
            return null;
        }

        final TSqlMetaData sqlMetaData = (TSqlMetaData) tbase;

        if (logger.isDebugEnabled()) {
            logger.debug("Received SqlMetaData:{}", sqlMetaData);
        }


        try {
            sqlMetaDataDao.insert(sqlMetaData);
        } catch (Exception e) {
            logger.warn("{} handler error. Caused:{}", this.getClass(), e.getMessage(), e);
            TResult result = new TResult(false);
            result.setMessage(e.getMessage());
            return result;
        }
        return new TResult(true);
    }
    
    public void setSqlMetaDataDao(SqlMetaDataDao sqlMetaDataDao) {
        this.sqlMetaDataDao = sqlMetaDataDao;
    }
}
