package com.m2u.eyelink.collector.bo.serializer.stat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.m2u.eyelink.collector.bo.codec.stat.TransactionEncoder;
import com.m2u.eyelink.collector.bo.stat.TransactionBo;

@Component
public class TransactionSerializer extends AgentStatSerializer<TransactionBo> {

    @Autowired
    public TransactionSerializer(TransactionEncoder transactionEncoder) {
        super(transactionEncoder);
    }
}
