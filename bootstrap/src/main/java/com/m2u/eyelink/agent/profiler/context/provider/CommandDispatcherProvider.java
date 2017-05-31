package com.m2u.eyelink.agent.profiler.context.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.m2u.eyelink.agent.profiler.context.active.ActiveTraceRepository;
import com.m2u.eyelink.agent.profiler.receiver.CommandDispatcher;
import com.m2u.eyelink.agent.profiler.receiver.ProfilerCommandLocatorBuilder;
import com.m2u.eyelink.agent.profiler.receiver.ProfilerCommandServiceLocator;
import com.m2u.eyelink.agent.profiler.receiver.service.ActiveThreadService;
import com.m2u.eyelink.agent.profiler.receiver.service.EchoService;
import com.m2u.eyelink.config.ProfilerConfig;

public class CommandDispatcherProvider implements Provider<CommandDispatcher> {

    private final ProfilerConfig profilerConfig;
    private final ActiveTraceRepository activeTraceRepository;

    @Inject
    public CommandDispatcherProvider(ProfilerConfig profilerConfig, Provider<ActiveTraceRepository> activeTraceRepositoryProvider) {
        if (profilerConfig == null) {
            throw new NullPointerException("profilerConfig must not be null");
        }
        if (activeTraceRepositoryProvider == null) {
            throw new NullPointerException("activeTraceRepositoryProvider must not be null");
        }

        this.profilerConfig = profilerConfig;
        this.activeTraceRepository = activeTraceRepositoryProvider.get();
    }

    @Override
    public CommandDispatcher get() {
        ProfilerCommandLocatorBuilder builder = new ProfilerCommandLocatorBuilder();
        builder.addService(new EchoService());
        if (activeTraceRepository != null) {
            ActiveThreadService activeThreadService = new ActiveThreadService(profilerConfig, activeTraceRepository);
            builder.addService(activeThreadService);
        }

        ProfilerCommandServiceLocator commandServiceLocator = builder.build();
        CommandDispatcher commandDispatcher = new CommandDispatcher(commandServiceLocator);
        return commandDispatcher;
    }
}
