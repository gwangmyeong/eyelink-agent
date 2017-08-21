package com.m2u.eyelink.collector.bo.codec.stat.v2;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.m2u.eyelink.collector.bo.codec.stat.AgentStatCodec;
import com.m2u.eyelink.collector.bo.codec.stat.AgentStatDataPointCodec;
import com.m2u.eyelink.collector.bo.codec.stat.header.AgentStatHeaderDecoder;
import com.m2u.eyelink.collector.bo.codec.stat.header.AgentStatHeaderEncoder;
import com.m2u.eyelink.collector.bo.codec.stat.header.BitCountingHeaderDecoder;
import com.m2u.eyelink.collector.bo.codec.stat.header.BitCountingHeaderEncoder;
import com.m2u.eyelink.collector.bo.codec.strategy.EncodingStrategy;
import com.m2u.eyelink.collector.bo.codec.strategy.StrategyAnalyzer;
import com.m2u.eyelink.collector.bo.codec.strategy.UnsignedLongEncodingStrategy;
import com.m2u.eyelink.collector.bo.serializer.stat.AgentStatDecodingContext;
import com.m2u.eyelink.collector.bo.serializer.stat.AgentStatUtils;
import com.m2u.eyelink.collector.bo.stat.CpuLoadBo;
import com.m2u.eyelink.util.Buffer;
import com.m2u.eyelink.util.CollectionUtils;

@Component("cpuLoadCodecV2")
public class CpuLoadCodecV2 implements AgentStatCodec<CpuLoadBo> {

    private static final byte VERSION = 2;

    private final AgentStatDataPointCodec codec;

    @Autowired
    public CpuLoadCodecV2(AgentStatDataPointCodec codec) {
        Assert.notNull(codec, "agentStatDataPointCodec must not be null");
        this.codec = codec;
    }

    @Override
    public byte getVersion() {
        return VERSION;
    }

    @Override
    public void encodeValues(Buffer valueBuffer, List<CpuLoadBo> cpuLoadBos) {
        if (CollectionUtils.isEmpty(cpuLoadBos)) {
            throw new IllegalArgumentException("cpuLoadBos must not be empty");
        }
        final int numValues = cpuLoadBos.size();
        valueBuffer.putVInt(numValues);

        List<Long> startTimestamps = new ArrayList<Long>(numValues);
        List<Long> timestamps = new ArrayList<Long>(numValues);
        UnsignedLongEncodingStrategy.Analyzer.Builder jvmCpuLoadAnalyzerBuilder = new UnsignedLongEncodingStrategy.Analyzer.Builder();
        UnsignedLongEncodingStrategy.Analyzer.Builder systemCpuLoadAnalyzerBuilder = new UnsignedLongEncodingStrategy.Analyzer.Builder();
        for (CpuLoadBo cpuLoadBo : cpuLoadBos) {
            startTimestamps.add(cpuLoadBo.getStartTimestamp());
            timestamps.add(cpuLoadBo.getTimestamp());
            jvmCpuLoadAnalyzerBuilder.addValue(AgentStatUtils.convertDoubleToLong(cpuLoadBo.getJvmCpuLoad()));
            systemCpuLoadAnalyzerBuilder.addValue(AgentStatUtils.convertDoubleToLong(cpuLoadBo.getSystemCpuLoad()));
        }
        this.codec.encodeValues(valueBuffer, UnsignedLongEncodingStrategy.REPEAT_COUNT, startTimestamps);
        this.codec.encodeTimestamps(valueBuffer, timestamps);
        this.encodeDataPoints(valueBuffer, jvmCpuLoadAnalyzerBuilder.build(), systemCpuLoadAnalyzerBuilder.build());
    }

    private void encodeDataPoints(
            Buffer valueBuffer,
            StrategyAnalyzer<Long> jvmCpuLoadStrategyAnalyzer,
            StrategyAnalyzer<Long> systemCpuLoadStrategyAnalyzer) {
        // encode header
        AgentStatHeaderEncoder headerEncoder = new BitCountingHeaderEncoder();
        headerEncoder.addCode(jvmCpuLoadStrategyAnalyzer.getBestStrategy().getCode());
        headerEncoder.addCode(systemCpuLoadStrategyAnalyzer.getBestStrategy().getCode());
        final byte[] header = headerEncoder.getHeader();
        valueBuffer.putPrefixedBytes(header);
        // encode values
        this.codec.encodeValues(valueBuffer, jvmCpuLoadStrategyAnalyzer.getBestStrategy(), jvmCpuLoadStrategyAnalyzer.getValues());
        this.codec.encodeValues(valueBuffer, systemCpuLoadStrategyAnalyzer.getBestStrategy(), systemCpuLoadStrategyAnalyzer.getValues());
    }

    @Override
    public List<CpuLoadBo> decodeValues(Buffer valueBuffer, AgentStatDecodingContext decodingContext) {
        final String agentId = decodingContext.getAgentId();
        final long baseTimestamp = decodingContext.getBaseTimestamp();
        final long timestampDelta = decodingContext.getTimestampDelta();
        final long initialTimestamp = baseTimestamp + timestampDelta;

        int numValues = valueBuffer.readVInt();
        List<Long> startTimestamps = this.codec.decodeValues(valueBuffer, UnsignedLongEncodingStrategy.REPEAT_COUNT, numValues);
        List<Long> timestamps = this.codec.decodeTimestamps(initialTimestamp, valueBuffer, numValues);

        // decode headers
        final byte[] header = valueBuffer.readPrefixedBytes();
        AgentStatHeaderDecoder headerDecoder = new BitCountingHeaderDecoder(header);
        EncodingStrategy<Long> jvmCpuLoadEncodingStrategy = UnsignedLongEncodingStrategy.getFromCode(headerDecoder.getCode());
        EncodingStrategy<Long> systemCpuLoadEncodingStrategy = UnsignedLongEncodingStrategy.getFromCode(headerDecoder.getCode());
        // decode values
        List<Long> jvmCpuLoads = this.codec.decodeValues(valueBuffer, jvmCpuLoadEncodingStrategy, numValues);
        List<Long> systemCpuLoads = this.codec.decodeValues(valueBuffer, systemCpuLoadEncodingStrategy, numValues);

        List<CpuLoadBo> cpuLoadBos = new ArrayList<CpuLoadBo>(numValues);
        for (int i = 0; i < numValues; ++i) {
            CpuLoadBo cpuLoadBo = new CpuLoadBo();
            cpuLoadBo.setAgentId(agentId);
            cpuLoadBo.setStartTimestamp(startTimestamps.get(i));
            cpuLoadBo.setTimestamp(timestamps.get(i));
            cpuLoadBo.setJvmCpuLoad(AgentStatUtils.convertLongToDouble(jvmCpuLoads.get(i)));
            cpuLoadBo.setSystemCpuLoad(AgentStatUtils.convertLongToDouble(systemCpuLoads.get(i)));
            cpuLoadBos.add(cpuLoadBo);
        }
        return cpuLoadBos;
    }
}
