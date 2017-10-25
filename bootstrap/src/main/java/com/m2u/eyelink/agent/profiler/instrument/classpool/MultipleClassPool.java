package com.m2u.eyelink.agent.profiler.instrument.classpool;


public interface MultipleClassPool {
	NamedClassPool getClassPool(ClassLoader classLoader);

}
