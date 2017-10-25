package com.m2u.eyelink.agent.profiler.plugin;

import com.m2u.eyelink.agent.plugin.ProfilerPlugin;
import com.m2u.eyelink.agent.profiler.instrument.ClassInjector;

public interface PluginSetup {
    SetupResult setupPlugin(ProfilerPlugin plugin, ClassInjector classInjector);
}
