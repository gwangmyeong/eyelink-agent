package com.m2u.eyelink.context;

import java.nio.ByteBuffer;
import java.util.List;


public class TSpan {
	  private String agentId; // required
	  private String applicationName; // required
	  private long agentStartTime; // required
	  private ByteBuffer transactionId; // required
	  private long spanId; // required
	  private long parentSpanId; // optional
	  private long startTime; // required
	  private int elapsed; // optional
	  private String rpc; // optional
	  private short serviceType; // required
	  private String endPoint; // optional
	  private String remoteAddr; // optional
//	  private List<TAnnotation> annotations; // optional
	  private short flag; // optional
	  private int err; // optional
	  private List<TSpanEvent> spanEventList; // optional
	  private String parentApplicationName; // optional
	  private short parentApplicationType; // optional
	  private String acceptorHost; // optional
	  private int apiId; // optional
//	  private TIntStringValue exceptionInfo; // optional
	  private short applicationServiceType; // optional
	  private byte loggingTransactionInfo; // optional

}
