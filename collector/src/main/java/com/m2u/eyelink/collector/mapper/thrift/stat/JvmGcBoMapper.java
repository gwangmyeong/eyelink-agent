package com.m2u.eyelink.collector.mapper.thrift.stat;

import org.springframework.stereotype.Component;

import com.m2u.eyelink.collector.bo.stat.JvmGcBo;
import com.m2u.eyelink.collector.bo.stat.JvmGcType;
import com.m2u.eyelink.collector.mapper.thrift.ThriftBoMapper;
import com.m2u.eyelink.context.thrift.TJvmGc;

@Component
public class JvmGcBoMapper implements ThriftBoMapper<JvmGcBo, TJvmGc> {

    @Override
    public JvmGcBo map(TJvmGc tJvmGc) {
        JvmGcBo jvmGcBo = new JvmGcBo();
        jvmGcBo.setGcType(JvmGcType.valueOf(tJvmGc.getType().name()));
        jvmGcBo.setHeapUsed(tJvmGc.getJvmMemoryHeapUsed());
        jvmGcBo.setHeapMax(tJvmGc.getJvmMemoryHeapMax());
        jvmGcBo.setNonHeapUsed(tJvmGc.getJvmMemoryNonHeapUsed());
        jvmGcBo.setNonHeapMax(tJvmGc.getJvmMemoryNonHeapMax());
        jvmGcBo.setGcOldCount(tJvmGc.getJvmGcOldCount());
        jvmGcBo.setGcOldTime(tJvmGc.getJvmGcOldTime());
        return jvmGcBo;
    }
}

