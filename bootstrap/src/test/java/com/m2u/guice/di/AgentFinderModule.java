package com.m2u.guice.di;

import com.google.inject.AbstractModule;

/**
 * @author baesunghan
 * AgentFinder에 대한 dependence가 될 객체 설정 - Spring의 @Configuration 과 거의 동일 
 */
public class AgentFinderModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(AgentFinder.class);
	}

}
