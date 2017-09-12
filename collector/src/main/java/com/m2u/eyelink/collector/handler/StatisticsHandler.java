package com.m2u.eyelink.collector.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.m2u.eyelink.collector.dao.MapResponseTimeDao;
import com.m2u.eyelink.collector.dao.MapStatisticsCalleeDao;
import com.m2u.eyelink.collector.dao.MapStatisticsCallerDao;
import com.m2u.eyelink.trace.ServiceType;

@Service
public class StatisticsHandler {

	@Autowired
	private MapStatisticsCalleeDao mapStatisticsCalleeDao;

	@Autowired
	private MapStatisticsCallerDao mapStatisticsCallerDao;

	@Autowired
	private MapResponseTimeDao mapResponseTimeDao;

	/**
	 * Calling MySQL from Tomcat generates the following message for the
	 * caller(Tomcat) :<br/>
	 * emeroad-app (TOMCAT) -> MySQL_DB_ID (MYSQL)[10.25.141.69:3306] <br/>
	 * <br/>
	 * The following message is generated for the callee(MySQL) :<br/>
	 * MySQL (MYSQL) <- emeroad-app (TOMCAT)[localhost:8080]
	 * 
	 * @param callerApplicationName
	 * @param callerServiceType
	 * @param calleeApplicationName
	 * @param calleeServiceType
	 * @param calleeHost
	 * @param elapsed
	 * @param isError
	 */
	public void updateCaller(String callerApplicationName, ServiceType callerServiceType, String callerAgentId,
			String calleeApplicationName, ServiceType calleeServiceType, String calleeHost, int elapsed,
			boolean isError) {
		mapStatisticsCallerDao.update(callerApplicationName, callerServiceType, callerAgentId, calleeApplicationName,
				calleeServiceType, calleeHost, elapsed, isError);
	}

	/**
	 * Calling MySQL from Tomcat generates the following message for the
	 * callee(MySQL) :<br/>
	 * MySQL_DB_ID (MYSQL) <- emeroad-app (TOMCAT)[localhost:8080] <br/>
	 * <br/>
	 * <br/>
	 * The following message is generated for the caller(Tomcat) :<br/>
	 * emeroad-app (TOMCAT) -> MySQL (MYSQL)[10.25.141.69:3306]
	 * 
	 * @param callerApplicationName
	 * @param callerServiceType
	 * @param calleeApplicationName
	 * @param calleeServiceType
	 * @param callerHost
	 * @param elapsed
	 * @param isError
	 */
	public void updateCallee(String calleeApplicationName, ServiceType calleeServiceType, String callerApplicationName,
			ServiceType callerServiceType, String callerHost, int elapsed, boolean isError) {
		mapStatisticsCalleeDao.update(calleeApplicationName, calleeServiceType, callerApplicationName,
				callerServiceType, callerHost, elapsed, isError);
	}

	public void updateResponseTime(String applicationName, ServiceType serviceType, String agentId, int elapsed,
			boolean isError) {
		mapResponseTimeDao.received(applicationName, serviceType, agentId, elapsed, isError);
	}

	// bsh
	public void putApplicationMap(String transactionId, String callerApplicationName, ServiceType callerServiceType, String callerAgentId,
			String calleeApplicationName, ServiceType calleeServiceType, String calleeHost, long startTime, int elapsed,
			boolean isError) {
		mapStatisticsCallerDao.insert(transactionId, callerApplicationName, callerServiceType, callerAgentId, calleeApplicationName,
				calleeServiceType, calleeHost, startTime, elapsed, isError);

	}
}
