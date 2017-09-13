package com.m2u.eyelink.collector.alarm;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

public class AgentAlarmTypeTest {

	@Test
	public void test() {
		double memPer = 75.12;
		if (memPer == AgentAlarmType.MEMORY_90.getCode()) {
			
		} else if (memPer > 90) {
			
		} else if (memPer > 80) {
			
		} else if (memPer > AgentAlarmType.MEMORY_70.getCode()) {
			System.out.println(memPer);
			Assert.assertTrue(true);
		} else {
			fail("Not yet implemented");
		}
	}

}
