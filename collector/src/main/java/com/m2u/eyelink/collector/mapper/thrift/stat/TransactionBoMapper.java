package com.m2u.eyelink.collector.mapper.thrift.stat;

import org.springframework.stereotype.Component;

import com.m2u.eyelink.collector.bo.stat.TransactionBo;
import com.m2u.eyelink.context.thrift.TTransaction;

@Component
public class TransactionBoMapper implements ThriftBoMapper<TransactionBo, TTransaction> {

    @Override
    public TransactionBo map(TTransaction tTransaction) {
        TransactionBo transaction = new TransactionBo();
        transaction.setSampledNewCount(tTransaction.getSampledNewCount());
        transaction.setSampledContinuationCount(tTransaction.getSampledContinuationCount());
        transaction.setUnsampledNewCount(tTransaction.getUnsampledNewCount());
        transaction.setUnsampledContinuationCount(tTransaction.getUnsampledContinuationCount());
        return transaction;
    }
}
