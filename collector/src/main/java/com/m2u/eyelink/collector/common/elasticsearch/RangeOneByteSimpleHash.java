package com.m2u.eyelink.collector.common.elasticsearch;

public class RangeOneByteSimpleHash {
    protected final int start;
    protected final int end;
    private int mod;
	
    public RangeOneByteSimpleHash(int start, int end, int maxBuckets) {
        if (maxBuckets < 1 || maxBuckets > 256) {
            throw new IllegalArgumentException("maxBuckets should be in 1..256 range");
        }
        this.start = start;
        this.end = end;
        // i.e. "real" maxBuckets value = maxBuckets or maxBuckets-1
        this.mod = maxBuckets;
    }

}
