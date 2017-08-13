package com.m2u.eyelink.collector.mapper.thrift.stat;

import org.springframework.stereotype.Component;

import com.m2u.eyelink.collector.bo.stat.CpuLoadBo;
import com.m2u.eyelink.context.thrift.TCpuLoad;

@Component
public class CpuLoadBoMapper implements ThriftBoMapper<CpuLoadBo, TCpuLoad> {

    @Override
    public CpuLoadBo map(TCpuLoad tCpuLoad) {
        CpuLoadBo cpuLoadBo = new CpuLoadBo();
        cpuLoadBo.setJvmCpuLoad(tCpuLoad.getJvmCpuLoad());
        cpuLoadBo.setSystemCpuLoad(tCpuLoad.getSystemCpuLoad());
        return cpuLoadBo;
    }
}
