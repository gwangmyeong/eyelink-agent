package com.m2u.eyelink.agent.profiler.context.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.m2u.eyelink.agent.profiler.sender.UdpDataSenderFactory;
import com.m2u.eyelink.config.ProfilerConfig;
import com.m2u.eyelink.sender.DataSender;

public class UdpStatDataSenderProvider implements Provider<DataSender> {

    private static final String threadName = "ELAgent-UdpStatDataExecutor";

    private final String ip;
    private final int port;
    private final int writeQueueSize;
    private final int timeout;
    private final int sendBufferSize;
    private final String senderType;

    @Inject
    public UdpStatDataSenderProvider(ProfilerConfig profilerConfig) {
        if (profilerConfig == null) {
            throw new NullPointerException("profilerConfig must not be null");
        }
        this.ip = profilerConfig.getCollectorStatServerIp();
        this.port = profilerConfig.getCollectorStatServerPort();
        this.writeQueueSize = profilerConfig.getStatDataSenderWriteQueueSize();
        this.timeout = profilerConfig.getStatDataSenderSocketTimeout();
        this.sendBufferSize = profilerConfig.getStatDataSenderSocketSendBufferSize();
        this.senderType = profilerConfig.getStatDataSenderSocketType();
    }



    @Override
    public DataSender get() {
        UdpDataSenderFactory factory = new UdpDataSenderFactory(ip, port, threadName, writeQueueSize, timeout, sendBufferSize);
        return factory.create(senderType);
    }

    @Override
    public String toString() {
        return "UdpStatDataSenderProvider{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", writeQueueSize=" + writeQueueSize +
                ", timeout=" + timeout +
                ", sendBufferSize=" + sendBufferSize +
                ", senderType='" + senderType + '\'' +
                '}';
    }
}
