package com.m2u.eyelink.common.service;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.trace.ServiceType;

public class ServiceTypeRegistryTest {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Test
	public void findServiceType() {
		DefaultServiceTypeRegistryService serviceTypeRegistryService = new DefaultServiceTypeRegistryService();
		short code = 5050;
		ServiceType serviceType = serviceTypeRegistryService.findServiceType(code);
		boolean find = false;
		if (serviceType.getCode() == code) {
			find = true;
		}
		Assert.assertTrue(find);

	}
	@Test
	public void findServiceTypeByName() {
		DefaultServiceTypeRegistryService serviceTypeRegistryService = new DefaultServiceTypeRegistryService();
		String name = "SPRING";
		ServiceType serviceType = serviceTypeRegistryService.findServiceTypeByName(name);
		boolean find = false;
		if (serviceType.getName().equals(name)) {
			find = true;
		}
		Assert.assertTrue(find);
		
	}
	@Test
	public void findDesc() {
		DefaultServiceTypeRegistryService serviceTypeRegistryService = new DefaultServiceTypeRegistryService();
		String desc = "UNKNOWN_DB";
		List<ServiceType> serviceTypeList = serviceTypeRegistryService.findDesc(desc);
		boolean find = false;
		for (ServiceType serviceType : serviceTypeList) {
			if (serviceType.getDesc().equals(desc)) {
				find = true;
			}
		}
		Assert.assertTrue(find);
		
		try {
			serviceTypeList.add(ServiceType.INTERNAL_METHOD);
			Assert.fail();
		} catch (Exception ignored) {
		}
	}

}
