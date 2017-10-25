package com.m2u.eyelink.collector.bo.codec.stat.header;

import java.util.BitSet;

public class BitCountingHeaderDecoder implements AgentStatHeaderDecoder {

    private static final int NUM_BITS_PER_BYTE = 8;

    private final BitSet headerBitSet;
    private int position = 0;

    public BitCountingHeaderDecoder(byte[] header) {
        headerBitSet = new BitSet();
        // strictly follows JDK 7's BitSet.valueOf(byte[])
        for (int i = 0; i < header.length * NUM_BITS_PER_BYTE; i++) {
            byte currentBits = header[i / NUM_BITS_PER_BYTE];
            int bitMask = 1 << (i % NUM_BITS_PER_BYTE);
            if ((currentBits & bitMask) > 0) {
                headerBitSet.set(i);
            }
        }
        // use below when using JDK 7+
//        this.headerBitSet = BitSet.valueOf(header);
    }

    @Override
    public int getCode() {
        int fromIndex = this.position;
        int toIndex = this.headerBitSet.nextClearBit(this.position);
        int numBitsSet = this.headerBitSet.get(fromIndex, toIndex).cardinality();
        this.position = toIndex + 1;
        return numBitsSet;
    }
}
