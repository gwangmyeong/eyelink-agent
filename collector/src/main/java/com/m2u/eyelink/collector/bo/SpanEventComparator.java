package com.m2u.eyelink.collector.bo;

import java.util.Comparator;

import com.m2u.eyelink.collector.util.IntegerUtils;
import com.m2u.eyelink.collector.util.ShortUtils;

public class SpanEventComparator implements Comparator<SpanEventBo> {

    public static final SpanEventComparator INSTANCE = new SpanEventComparator();

    @Override
    public int compare(SpanEventBo o1, SpanEventBo o2) {
        final int sequenceCompare = ShortUtils.compare(o1.getSequence(), o2.getSequence());
        if (sequenceCompare != 0) {
            return sequenceCompare;
        }
        final int asyncId1 = o1.getAsyncId();
        final int asyncId2 = o2.getAsyncId();
        final int asyncIdCompare = IntegerUtils.compare(asyncId1, asyncId2);
        if (asyncIdCompare != 0) {
//                bug Comparison method violates its general contract!
//                TODO temporary fix
//                if (asyncId1 == -1) {
//                    return -1;
//                }
//                if (asyncId2 == -1) {
//                    return -1;
//                }
            return asyncIdCompare;
        }
        return IntegerUtils.compare(o1.getAsyncSequence(), o2.getAsyncSequence());
    }
}
