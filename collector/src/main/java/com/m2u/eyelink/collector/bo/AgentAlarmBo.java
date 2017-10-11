package com.m2u.eyelink.collector.bo;

public class AgentAlarmBo {
	private String applicationType;
	private String agentId;
	private String startTimestamp;
	private String timestamp;
	private String alarmType;
	
	public String getApplicationType() {
		return applicationType;
	}

	public void setApplicationType(String applicationType) {
		this.applicationType = applicationType;
	}

	public String getStartTimestamp() {
		return startTimestamp;
	}

	public void setStartTimestamp(String startTimestamp) {
		this.startTimestamp = startTimestamp;
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

	public String getAlarmTypeName() {
		return alarmTypeName;
	}

	public void setAlarmTypeName(String alarmTypeName) {
		this.alarmTypeName = alarmTypeName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	private String alarmTypeName;
	private String message;	
	
    @Override
    public String toString() {
        return "AgentAlarmBo{" +
                "applicationType='" + applicationType + '\'' +
                "agentId='" + getAgentId() + '\'' +
                "startTimestamp='" + startTimestamp + '\'' +
                "timestamp='" + timestamp + '\'' +
                "alarmType='" + alarmType + '\'' +
                "alarmTypeName='" + alarmTypeName + '\'' +
                ", message=" + message +
                '}';
    }

	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}
	
}
