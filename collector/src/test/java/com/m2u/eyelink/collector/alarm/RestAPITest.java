package com.m2u.eyelink.collector.alarm;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;


public class RestAPITest {
	private static final Logger log = LoggerFactory.getLogger(RestAPITest.class);
	private final String svr = "http://localhost:5223";
	
//	@Test
//    public void restAPI_GetTest() {
//        RestTemplate restTemplate = new RestTemplate();
//        Quote quote = restTemplate.getForObject(svr + "/management/restapi/getAlarmList", Quote.class);
//        log.info(quote.toString());
//        assertEquals("0000", quote.getRtnCode().getCode());
//    }
	
//	@Test
	public void restAPI_PostTest() throws ParseException {
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		final java.util.Date dateObj = new Date();
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));   // This line converts the given date into UTC time zone
		String timestamp = sdf.format(dateObj);
		System.out.println(timestamp);
		
		RestTemplate restTemplate = new RestTemplate();
		Request req = new Request();
		req.setApplicationType("ELAGENT");
		req.setAgentId("junit_test");
		req.setAlarmType("CPU_90");
		req.setAlamrtypeName("");
		req.setMessage("CPU over 90%");
		req.setTimestamp(timestamp);
	      
		Quote quote = restTemplate.postForObject(svr + "/socketapp/restapi/Alarm/100", req, Quote.class);
		log.info(quote.toString());
		assertEquals("0000", quote.getRtnCode().getCode());
	}

}
