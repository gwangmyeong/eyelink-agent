package com.m2u.eyelink.collector.common.elasticsearch.span;

public interface CallTree extends Iterable<CallTreeNode> {

    CallTreeNode getRoot();

    CallTreeIterator iterator();

    boolean isEmpty();

    void add(CallTree tree);

    void add(int parentDepth, CallTree tree);

    void add(final int depth, final SpanAlign spanAlign);
    
    void sort();
}