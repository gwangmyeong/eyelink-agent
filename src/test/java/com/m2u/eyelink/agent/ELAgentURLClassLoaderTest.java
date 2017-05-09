package com.m2u.eyelink.agent;

import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

public class ELAgentURLClassLoaderTest {

	@Test
	public void testOnLoadClass() throws Exception {

		ELAgentURLClassLoader cl = new ELAgentURLClassLoader(new URL[] {},
				Thread.currentThread().getContextClassLoader());
		try {
			cl.loadClass("test");
			Assert.fail();
		} catch (ClassNotFoundException ignored) {
		}

		// try {
		// cl.loadClass("com.navercorp.pinpoint.profiler.DefaultAgent");
		// } catch (ClassNotFoundException e) {
		//
		// }
		// should be able to test using the above code, but it is not possible
		// from bootstrap testcase.
		// it could be possible by specifying the full path to the URL
		// classloader, but it would be harder to maintain.
		// for now, just test if DefaultAgent is specified to be loaded
		Assert.assertTrue(cl
				.onLoadClass("com.navercorp.pinpoint.profiler.DefaultAgent"));
	}
}
