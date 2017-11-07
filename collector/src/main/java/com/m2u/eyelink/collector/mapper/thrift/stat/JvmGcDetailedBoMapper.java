package com.m2u.eyelink.collector.mapper.thrift.stat;

import org.springframework.stereotype.Component;

import com.m2u.eyelink.collector.bo.stat.JvmGcDetailedBo;
import com.m2u.eyelink.collector.mapper.thrift.ThriftBoMapper;
import com.m2u.eyelink.thrift.TJvmGcDetailed;


@Component
public class JvmGcDetailedBoMapper implements ThriftBoMapper<JvmGcDetailedBo, TJvmGcDetailed> {

    @Override
    public JvmGcDetailedBo map(TJvmGcDetailed tJvmGcDetailed) {
        JvmGcDetailedBo jvmGcDetailedBo = new JvmGcDetailedBo();
        jvmGcDetailedBo.setGcNewCount(tJvmGcDetailed.getJvmGcNewCount());
        jvmGcDetailedBo.setGcNewTime(tJvmGcDetailed.getJvmGcNewCount());
        jvmGcDetailedBo.setCodeCacheUsed(tJvmGcDetailed.getJvmPoolCodeCacheUsed());
        jvmGcDetailedBo.setNewGenUsed(tJvmGcDetailed.getJvmPoolNewGenUsed());
        jvmGcDetailedBo.setOldGenUsed(tJvmGcDetailed.getJvmPoolOldGenUsed());
        jvmGcDetailedBo.setSurvivorSpaceUsed(tJvmGcDetailed.getJvmPoolSurvivorSpaceUsed());
        jvmGcDetailedBo.setPermGenUsed(tJvmGcDetailed.getJvmPoolPermGenUsed());
        jvmGcDetailedBo.setMetaspaceUsed(tJvmGcDetailed.getJvmPoolMetaspaceUsed());
        return jvmGcDetailedBo;
    }
}
