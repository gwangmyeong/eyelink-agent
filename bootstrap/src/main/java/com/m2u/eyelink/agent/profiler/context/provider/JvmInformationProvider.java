package com.m2u.eyelink.agent.profiler.context.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.m2u.eyelink.agent.profiler.JvmInformation;
import com.m2u.eyelink.agent.profiler.monitor.metric.gc.GarbageCollectorMetric;
import com.m2u.eyelink.agent.profiler.monitor.metric.gc.UnknownGarbageCollectorMetric;
import com.m2u.eyelink.util.JvmUtils;
import com.m2u.eyelink.util.SystemPropertyKey;

public class JvmInformationProvider implements Provider<JvmInformation> {

    private final String jvmVersion;
    private final GarbageCollectorMetric garbageCollectorMetric;


    @Inject
    public JvmInformationProvider(GarbageCollectorMetric garbageCollectorMetric) {
        this.jvmVersion = JvmUtils.getSystemProperty(SystemPropertyKey.JAVA_VERSION);
        this.garbageCollectorMetric = garbageCollectorMetric;
    }

    public JvmInformationProvider() {
        this(new UnknownGarbageCollectorMetric());
    }

    public JvmInformation get() {
        return new JvmInformation(this.jvmVersion, this.garbageCollectorMetric.gcType().getValue());
    }
}
