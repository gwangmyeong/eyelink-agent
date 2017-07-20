package com.m2u.guice.di;

import static org.junit.Assert.*;

import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class HollyWoodServiceTest {
	private Injector injector = null;
	
	@Before
	public void setUp() {
		injector = Guice.createInjector(new AgentFinderModule());
		assertThat(injector, is(not(nullValue())));
	}
	
	@Test
	public void nullTest() {
	    String str = null;
	    assertThat(str,  is(nullValue()));
	}
	
	@Test
	public void getHollywoodService() {
		HollyWoodService ho = injector.getInstance(HollyWoodService.class);
		assertThat(ho, is(not(nullValue())));
	}

}
