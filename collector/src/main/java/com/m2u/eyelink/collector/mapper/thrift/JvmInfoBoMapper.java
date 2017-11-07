package com.m2u.eyelink.collector.mapper.thrift;

import org.springframework.stereotype.Component;

import com.m2u.eyelink.collector.bo.JvmInfoBo;
import com.m2u.eyelink.thrift.TJvmInfo;

@Component
public class JvmInfoBoMapper implements ThriftBoMapper<JvmInfoBo, TJvmInfo> {
    @Override
    public JvmInfoBo map(TJvmInfo thriftObject) {
        short version = thriftObject.getVersion();
        String jvmVersion = thriftObject.getVmVersion();
        String gcTypeName = thriftObject.getGcType().name();
        JvmInfoBo jvmInfoBo = new JvmInfoBo(version);
        jvmInfoBo.setJvmVersion(jvmVersion);
        jvmInfoBo.setGcTypeName(gcTypeName);
        return jvmInfoBo;
    }
}
