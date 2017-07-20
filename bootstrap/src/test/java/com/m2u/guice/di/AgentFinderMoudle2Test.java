package com.m2u.guice.di;

import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class AgentFinderMoudle2Test {
	private Injector injector = null;
	@Before
	public void setUp() {
		injector = Guice.createInjector(new AgentFinderModule2());
		assertThat(injector, is(not(nullValue())));
	}
	/**
	 * Singleton으로 설정된 객체는 호출될 때, 기존의 객체가 있는 경우에는 그 객체를 계속해서 사용하게 됩니다. 그렇지만, 아무것도 설정되지 않은 객체의 경우에는 마치 new를 통해서 생성되는 객체와 동일한 패턴을 따르게 됩니다.
	 * 출처: http://netframework.tistory.com/entry/Google-Guice를-이용한-DI-1 [Programming is Fun]
	 */
	@Test
	public void getHollywoodService() {
		HollyWoodService ho1 = injector.getInstance(HollyWoodService.class);
		assertThat(ho1, is(not(nullValue())));
		HollyWoodService ho2 = injector.getInstance(HollyWoodService.class);
		System.out.println(ho1);
		System.out.println(ho2);
		assertThat(ho1 != ho2, is(true));
		
		System.out.println(ho1.getAgentFinder());
		System.out.println(ho2.getAgentFinder());
		assertThat(ho1.getAgentFinder() == ho2.getAgentFinder(), is(true));
		
		// FIXME AgentFinderImpl이 나와야 하는데 결과값에 AgentFinder가 각각으로 보여짐.
		// 실행 결
//		com.m2u.guice.di.HollyWoodService@13eb8acf
//		com.m2u.guice.di.HollyWoodService@51c8530f
//		com.m2u.guice.di.AgentFinder@c81cdd1
//		com.m2u.guice.di.AgentFinder@1fc2b765
	}

}
