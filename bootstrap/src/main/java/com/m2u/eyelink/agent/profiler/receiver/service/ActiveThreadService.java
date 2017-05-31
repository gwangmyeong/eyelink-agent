package com.m2u.eyelink.agent.profiler.receiver.service;

import java.util.ArrayList;
import java.util.List;

import com.m2u.eyelink.agent.profiler.context.active.ActiveTraceRepository;
import com.m2u.eyelink.agent.profiler.receiver.ProfilerCommandService;
import com.m2u.eyelink.agent.profiler.receiver.ProfilerCommandServiceGroup;
import com.m2u.eyelink.config.ProfilerConfig;

public class ActiveThreadService implements ProfilerCommandServiceGroup {

    private final List<ProfilerCommandService> serviceList;

    public ActiveThreadService(ProfilerConfig profilerConfig, ActiveTraceRepository activeTraceRepository) {
        serviceList = new ArrayList<ProfilerCommandService>();

        if (!profilerConfig.isTcpDataSenderCommandActiveThreadEnable()) {
            return;
        }

        if (profilerConfig.isTcpDataSenderCommandActiveThreadCountEnable()) {
            serviceList.add(new ActiveThreadCountService(activeTraceRepository));
        }
        if (profilerConfig.isTcpDataSenderCommandActiveThreadLightDumpEnable()) {
            serviceList.add(new ActiveThreadLightDumpService(activeTraceRepository));
        }
        if (profilerConfig.isTcpDataSenderCommandActiveThreadDumpEnable()) {
            serviceList.add(new ActiveThreadDumpService(activeTraceRepository));
        }
    }

    @Override
    public List<ProfilerCommandService> getCommandServiceList() {
        return serviceList;
    }

}
