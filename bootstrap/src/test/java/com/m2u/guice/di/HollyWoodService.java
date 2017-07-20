package com.m2u.guice.di;

import com.google.inject.Inject;


/**
 * @author baesunghan
 * @Inject를 이용해서 서비스 받을 대상 
 */
public class HollyWoodService {
	private AgentFinder agentFinder;
	
	/**
	 * @param agentFinder
	 * Constructor Injection
	 */
	@Inject
	public HollyWoodService(AgentFinder agentFinder) {
		this.agentFinder = agentFinder;
		agentFinder.doSomeThing();
	}

	public AgentFinder getAgentFinder() {
		return this.agentFinder;
	}
}
