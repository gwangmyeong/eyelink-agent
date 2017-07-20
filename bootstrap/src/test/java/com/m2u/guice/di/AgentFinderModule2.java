package com.m2u.guice.di;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

public class AgentFinderModule2 extends AbstractModule {

	@Override
	protected void configure() {
		bind(AgentFinder.class)
			.annotatedWith(Names.named("primary"))
			.to(AgentFinderImpl.class)
			.in(Singleton.class);
		bind(HollyWoodService.class);
		
	}
	
}
