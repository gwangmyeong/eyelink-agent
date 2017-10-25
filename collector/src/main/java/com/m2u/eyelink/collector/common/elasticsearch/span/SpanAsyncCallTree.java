package com.m2u.eyelink.collector.common.elasticsearch.span;

public class SpanAsyncCallTree implements CallTree {

    private final SpanCallTree tree;

    public SpanAsyncCallTree(final SpanAlign spanAlign) {
        tree = new SpanCallTree(spanAlign);
    }

    @Override
    public CallTreeNode getRoot() {
        if (!tree.getRoot().hasChild()) {
            return null;
        }

        return tree.getRoot().getChild();
    }

    @Override
    public CallTreeIterator iterator() {
        return new CallTreeIterator(getRoot());
    }

    @Override
    public boolean isEmpty() {
        CallTreeNode root = getRoot();
        if (root == null) {
            return true;
        }
        return root.getValue() == null;
    }

    @Override
    public void add(CallTree tree) {
        this.tree.add(tree);
    }

    @Override
    public void add(int parentDepth, CallTree tree) {
        this.tree.add(parentDepth, tree);
    }

    @Override
    public void add(int depth, SpanAlign spanAlign) {
        tree.add(depth, spanAlign);
    }

    @Override
    public void sort() {
        tree.sort();
    }
}