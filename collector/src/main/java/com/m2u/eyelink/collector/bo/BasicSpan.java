package com.m2u.eyelink.collector.bo;

import com.m2u.eyelink.util.TransactionId;

public interface BasicSpan {

    String getAgentId();
    void setAgentId(String agentId);

    String getApplicationId();
    void  setApplicationId(String applicationId);

    long getAgentStartTime();
    void setAgentStartTime(long agentStartTime);

    long getSpanId();
    void setSpanId(long spanId);

    TransactionId getTransactionId();
//    void setTransactionId(TransactionId transactionId);


//    List<SpanEventBo> getSpanEventBoList();
}
