package com.m2u.eyelink.collector.bo.serializer.trace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.m2u.eyelink.collector.bo.serializer.RowKeyEncoder;
import com.m2u.eyelink.collector.common.elasticsearch.AbstractRowKeyDistributor;
import com.m2u.eyelink.util.BytesUtils;
import com.m2u.eyelink.util.TransactionId;

import com.m2u.eyelink.common.ELAgentConstants;

@Component
public class TraceRowKeyEncoderV2 implements RowKeyEncoder<TransactionId> {

    public static final int AGENT_NAME_MAX_LEN = ELAgentConstants.AGENT_NAME_MAX_LEN;
    public static final int DISTRIBUTE_HASH_SIZE = 1;

    private final AbstractRowKeyDistributor rowKeyDistributor;


    @Autowired
    public TraceRowKeyEncoderV2(@Qualifier("traceV2Distributor") AbstractRowKeyDistributor rowKeyDistributor) {
        if (rowKeyDistributor == null) {
            throw new NullPointerException("rowKeyDistributor must not be null");
        }

        this.rowKeyDistributor = rowKeyDistributor;
    }

    public byte[] encodeRowKey(TransactionId transactionId) {
        if (transactionId == null) {
            throw new NullPointerException("basicSpan must not be null");
        }

        byte[] rowKey = BytesUtils.stringLongLongToBytes(transactionId.getAgentId(), AGENT_NAME_MAX_LEN, transactionId.getAgentStartTime(), transactionId.getTransactionSequence());
        return wrapDistributedRowKey(rowKey);
    }

    private byte[] wrapDistributedRowKey(byte[] rowKey) {
        return rowKeyDistributor.getDistributedKey(rowKey);
    }
}
