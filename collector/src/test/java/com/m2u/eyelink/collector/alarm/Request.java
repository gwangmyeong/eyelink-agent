package com.m2u.eyelink.collector.alarm;

public class Request {

    private String applicationType;
    public String getApplicationType() {
		return applicationType;
	}


	public void setApplicationType(String applicationType) {
		this.applicationType = applicationType;
	}


	public String getAgentId() {
		return agentId;
	}


	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}


	public String getTimestamp() {
		return timestamp;
	}


	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}


	public String getAlarmType() {
		return alarmType;
	}


	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}


	public String getAlamrtypeName() {
		return alamrtypeName;
	}


	public void setAlamrtypeName(String alamrtypeName) {
		this.alamrtypeName = alamrtypeName;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}


	private String agentId;
    private String timestamp;
    private String alarmType;
    private String alamrtypeName;
    private String message;

    public Request() {
    }


    @Override
    public String toString() {
        return "Request{" +
                "agentId='" + agentId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
	
}
