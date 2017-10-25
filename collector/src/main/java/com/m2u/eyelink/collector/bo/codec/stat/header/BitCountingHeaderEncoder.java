package com.m2u.eyelink.collector.bo.codec.stat.header;

import java.util.BitSet;

public class BitCountingHeaderEncoder implements AgentStatHeaderEncoder {

    private static final int NUM_BITS_PER_BYTE = 8;

    private final BitSet headerBitSet = new BitSet();
    private int position = 0;

    @Override
    public void addCode(int code) {
        if (code < 0) {
            throw new IllegalArgumentException("code must be positive");
        }
        int fromIndex = this.position;
        int toIndex = this.position + code;
        this.headerBitSet.set(fromIndex, toIndex);
        this.position = toIndex + 1;
    }

    @Override
    public byte[] getHeader() {
        if (position == 0) {
            return new byte[0];
        }
        // strictly follows JDK 7's BitSet.toByteArray()
        int len = (headerBitSet.length() + (NUM_BITS_PER_BYTE - 1)) / NUM_BITS_PER_BYTE;
        byte[] header = new byte[len];
        for (int i = 0; i < len * NUM_BITS_PER_BYTE; ++i) {
            int index = i / NUM_BITS_PER_BYTE;
            int bitMask = (headerBitSet.get(i) ? 1 : 0) << (i % NUM_BITS_PER_BYTE);
            header[index] |= bitMask;
        }
        return header;
        // use below when using JDK 7+
//        return this.headerBitSet.toByteArray();
    }
}
