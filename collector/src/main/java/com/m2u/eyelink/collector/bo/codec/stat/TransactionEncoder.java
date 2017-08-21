package com.m2u.eyelink.collector.bo.codec.stat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.m2u.eyelink.collector.bo.stat.TransactionBo;

@Component
public class TransactionEncoder extends AgentStatEncoder<TransactionBo> {

    @Autowired
    public TransactionEncoder(@Qualifier("transactionCodecV2") AgentStatCodec<TransactionBo> transactionCodec) {
        super(transactionCodec);
    }
}
