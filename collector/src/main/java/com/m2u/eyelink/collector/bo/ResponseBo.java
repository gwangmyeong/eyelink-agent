package com.m2u.eyelink.collector.bo;

public class ResponseBo {
    private String code;
    private String message;

    public ResponseBo() {
    }


    @Override
    public String toString() {
        return "ResponseBo{" +
                "code='" + code + '\'' +
                ", message=" + message +
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
