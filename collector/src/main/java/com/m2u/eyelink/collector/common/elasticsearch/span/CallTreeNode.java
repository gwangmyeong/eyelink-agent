package com.m2u.eyelink.collector.common.elasticsearch.span;

public class CallTreeNode {

    private CallTreeNode parent;
    private CallTreeNode child;
    private CallTreeNode sibling;
    private final SpanAlign value;

    public CallTreeNode(final CallTreeNode parent, SpanAlign value) {
        this.parent = parent;
        this.value = value;
    }

    public void setParent(final CallTreeNode parent) {
        this.parent = parent;
    }

    public CallTreeNode getParent() {
        return parent;
    }

    public SpanAlign getValue() {
        return value;
    }

    public void setChild(final CallTreeNode child) {
        this.child = child;
    }

    public void setChild(final SpanAlign spanAlign) {
        this.child = new CallTreeNode(this, spanAlign);
    }

    public CallTreeNode getChild() {
        return this.child;
    }

    public boolean hasChild() {
        return this.child != null;
    }

    public void setSibling(final CallTreeNode sibling) {
        this.sibling = sibling;
    }

    public void setSibling(final SpanAlign spanAlign) {
        this.sibling = new CallTreeNode(parent, spanAlign);
    }

    public CallTreeNode getSibling() {
        return sibling;
    }

    public boolean hasSibling() {
        return this.sibling != null;
    }

    public int getDepth() {
        if (isRoot()) {
            return 0;
        }

        // change logic from recursive to loop, because of avoid call-stack-overflow.
        int depth = 1;
        CallTreeNode node = parent.getParent();
        while(node != null) {
            depth++;
            node = node.getParent();
        }

        return depth;
    }

    public boolean isRoot() {
        return parent == null;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{depth=");
        builder.append(getDepth());
        builder.append(", child=");
        builder.append(child != null ? true : false);
        builder.append(", sibling=");
        builder.append(sibling != null ? true : false);
        builder.append(", value=");
        builder.append(value);
        builder.append("}");
        return builder.toString();
    }
}