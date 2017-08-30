package com.m2u.eyelink.collector;

import static org.junit.Assert.assertEquals;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

import com.m2u.eyelink.collector.util.TimeUtils;

public class TimeUtilsTest {

	@Test
	public void convertEpochToTime() {
		long epoch = 1503920591337L;
		String dt_org = "2017-08-28T11:43:11.337";
		
		String dt = TimeUtils.convertEpochToDate(epoch);
		assertEquals(dt_org, dt);

	}
}
