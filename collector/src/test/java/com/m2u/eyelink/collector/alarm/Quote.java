package com.m2u.eyelink.collector.alarm;

//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//
//@JsonIgnoreProperties(ignoreUnknown = true)
public class Quote {

    private String type;
    private Value rtnCode;

    public Quote() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Quote{" +
                "type='" + type + '\'' +
                ", code=" + rtnCode.getCode() +
                '}';
    }

	public Value getRtnCode() {
		return rtnCode;
	}

	public void setRtnCode(Value rtnCode) {
		this.rtnCode = rtnCode;
	}


}