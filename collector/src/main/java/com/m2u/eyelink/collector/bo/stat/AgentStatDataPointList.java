package com.m2u.eyelink.collector.bo.stat;

import java.util.List;

public interface AgentStatDataPointList<E extends AgentStatDataPoint> extends AgentStatDataPoint {

    boolean add(E element);
    boolean remove(E element);
    int size();
    List<E> getList();

}
