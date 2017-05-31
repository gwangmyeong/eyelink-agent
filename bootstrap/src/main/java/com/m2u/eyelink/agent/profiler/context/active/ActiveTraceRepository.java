package com.m2u.eyelink.agent.profiler.context.active;

import java.util.List;

import com.m2u.eyelink.context.ActiveTrace;
import com.m2u.eyelink.context.ActiveTraceInfo;

public interface ActiveTraceRepository {

    void put(ActiveTrace activeTrace);

    ActiveTrace remove(Long key);

    List<ActiveTraceInfo> collect();

}
