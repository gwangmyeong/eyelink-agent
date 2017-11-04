package com.m2u.eyelink.collector.receiver;

import org.apache.thrift.TBase;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.m2u.eyelink.collector.handler.AgentInfoHandler;
import com.m2u.eyelink.collector.handler.RequestResponseHandler;
import com.m2u.eyelink.collector.handler.SimpleHandler;
import com.m2u.eyelink.thrift.TAgentInfo;
import com.m2u.eyelink.thrift.dto.TApiMetaData;
import com.m2u.eyelink.thrift.dto.TSqlMetaData;
import com.m2u.eyelink.thrift.dto.TStringMetaData;

public class TcpDispatchHandler extends AbstractDispatchHandler {

    @Autowired()
    @Qualifier("agentInfoHandler")
    private AgentInfoHandler agentInfoHandler;

    @Autowired()
    @Qualifier("sqlMetaDataHandler")
    private RequestResponseHandler sqlMetaDataHandler;

    @Autowired()
    @Qualifier("apiMetaDataHandler")
    private RequestResponseHandler apiMetaDataHandler;

    @Autowired()
    @Qualifier("stringMetaDataHandler")
    private RequestResponseHandler stringMetaDataHandler;



    public TcpDispatchHandler() {
        this.logger = LoggerFactory.getLogger(this.getClass());
    }


    @Override
    RequestResponseHandler getRequestResponseHandler(TBase<?, ?> tBase) {
        if (tBase instanceof TSqlMetaData) {
            return sqlMetaDataHandler;
        }
        if (tBase instanceof TApiMetaData) {
            return apiMetaDataHandler;
        }
        if (tBase instanceof TStringMetaData) {
            return stringMetaDataHandler;
        }
        if (tBase instanceof TAgentInfo) {
            return agentInfoHandler;
        }
        return null;
    }

    @Override
    SimpleHandler getSimpleHandler(TBase<?, ?> tBase) {

        if (tBase instanceof TAgentInfo) {
            return agentInfoHandler;
        }

        return null;
    }
}
