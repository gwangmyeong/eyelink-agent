package com.m2u.eyelink.collector.alarm;

//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//@JsonIgnoreProperties(ignoreUnknown = true)
public class Value {

    private String code;
    private String message;

    public Value() {
    }



    @Override
    public String toString() {
        return "Value{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }



	public String getCode() {
		return code;
	}



	public void setCode(String code) {
		this.code = code;
	}



	public String getMessage() {
		return message;
	}



	public void setMessage(String message) {
		this.message = message;
	}
}