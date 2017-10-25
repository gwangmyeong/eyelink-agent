package com.m2u.eyelink.agent.profiler.context.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.m2u.eyelink.agent.profiler.sender.UdpDataSenderFactory;
import com.m2u.eyelink.config.ProfilerConfig;
import com.m2u.eyelink.sender.DataSender;

public class UdpSpanDataSenderProvider implements Provider<DataSender> {

    private static final String threadName = "Pinpoint-UdpSpanDataExecutor";

    private final String ip;
    private final int port;
    private final int writeQueueSize;
    private final int timeout;
    private final int sendBufferSize;
    private final String senderType;

    @Inject
    public UdpSpanDataSenderProvider(ProfilerConfig profilerConfig) {
        if (profilerConfig == null) {
            throw new NullPointerException("profilerConfig must not be null");
        }
        this.ip = profilerConfig.getCollectorSpanServerIp();
        this.port = profilerConfig.getCollectorSpanServerPort();
        this.writeQueueSize = profilerConfig.getSpanDataSenderWriteQueueSize();
        this.timeout = profilerConfig.getSpanDataSenderSocketTimeout();
        this.sendBufferSize = profilerConfig.getSpanDataSenderSocketSendBufferSize();
        this.senderType = profilerConfig.getSpanDataSenderSocketType();
    }

    public UdpSpanDataSenderProvider(String ip, int port, int writeQueueSize, int timeout, int sendBufferSize, String senderType) {
        this.ip = ip;
        this.port = port;
        this.writeQueueSize = writeQueueSize;
        this.timeout = timeout;
        this.sendBufferSize = sendBufferSize;
        this.senderType = senderType;
    }


    @Override
    public DataSender get() {
        UdpDataSenderFactory factory = new UdpDataSenderFactory(ip, port, threadName, writeQueueSize, timeout, sendBufferSize);
        return factory.create(senderType);
    }


    @Override
    public String toString() {
        return "UdpSpanDataSenderProvider{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", writeQueueSize=" + writeQueueSize +
                ", timeout=" + timeout +
                ", sendBufferSize=" + sendBufferSize +
                ", senderType='" + senderType + '\'' +
                '}';
    }

}
