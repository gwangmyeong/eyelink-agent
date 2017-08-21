package com.m2u.eyelink.collector.bo;

public class AnnotationBo {

    private int key;
    private Object value;

    private boolean isAuthorized = true;
    
    public AnnotationBo() {
    }

    public int getKey() {
        return key;
    }


    public void setKey(int key) {
        this.key = key;
    }


    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public void setAuthorized(boolean isAuthorized) {
        this.isAuthorized = isAuthorized;
    }


    @Override
    public String toString() {
        return "AnnotationBo{" +
                "key=" + key +
                ", value=" + value +
                ", isAuthorized=" + isAuthorized +
                '}';
    }
}
