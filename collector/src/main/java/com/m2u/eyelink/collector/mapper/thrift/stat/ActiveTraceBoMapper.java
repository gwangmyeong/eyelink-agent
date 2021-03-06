package com.m2u.eyelink.collector.mapper.thrift.stat;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.m2u.eyelink.collector.bo.stat.ActiveTraceBo;
import com.m2u.eyelink.collector.mapper.thrift.ThriftBoMapper;
import com.m2u.eyelink.common.trace.SlotType;
import com.m2u.eyelink.thrift.TActiveTrace;
import com.m2u.eyelink.thrift.TActiveTraceHistogram;

@Component
public class ActiveTraceBoMapper implements ThriftBoMapper<ActiveTraceBo, TActiveTrace> {

    @Override
    public ActiveTraceBo map(TActiveTrace tActiveTrace) {
        TActiveTraceHistogram tActiveTraceHistogram = tActiveTrace.getHistogram();
        Map<String, Integer> activeTraceCounts = createActiveTraceCountMap(tActiveTraceHistogram.getActiveTraceCount());
        ActiveTraceBo activeTraceBo = new ActiveTraceBo();
        activeTraceBo.setVersion(tActiveTraceHistogram.getVersion());
        activeTraceBo.setHistogramSchemaType(tActiveTraceHistogram.getHistogramSchemaType());
        activeTraceBo.setActiveTraceCounts(activeTraceCounts);
        return activeTraceBo;
    }

    private Map<String, Integer> createActiveTraceCountMap(List<Integer> activeTraceCounts) {
        if (activeTraceCounts == null || activeTraceCounts.isEmpty()) {
            return Collections.emptyMap();
        } else {
            if (activeTraceCounts.size() != 4) {
                return Collections.emptyMap();
            } else {
                Map<String, Integer> activeTraceCountMap = new HashMap<String, Integer>();
                // TODO SlotType 값 String 변환 로직 점검.
                activeTraceCountMap.put(""+SlotType.FAST, activeTraceCounts.get(0));
                activeTraceCountMap.put(""+SlotType.NORMAL, activeTraceCounts.get(1));
                activeTraceCountMap.put(""+SlotType.SLOW, activeTraceCounts.get(2));
                activeTraceCountMap.put(""+SlotType.VERY_SLOW, activeTraceCounts.get(3));
                return activeTraceCountMap;
            }
        }
    }
}
