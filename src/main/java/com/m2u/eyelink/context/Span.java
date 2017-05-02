package com.m2u.eyelink.context;

import java.nio.ByteBuffer;

import com.m2u.eyelink.util.TransactionIdUtils;

public class Span extends TSpan implements FrameAttachment {
    private boolean timeRecording = true;
    private Object frameObject;
    
    public Span() {
    }

    public void recordTraceId(final TraceId traceId) {
        if (traceId == null) {
            throw new NullPointerException("traceId must not be null");
        }
        final String agentId = this.getAgentId();
        if (agentId == null) {
            throw new NullPointerException("agentId must not be null");
        }

        final String transactionAgentId = traceId.getAgentId();
        if (!agentId.equals(transactionAgentId)) {
            this.setTransactionId(TransactionIdUtils.formatByteBuffer(transactionAgentId, traceId.getAgentStartTime(), traceId.getTransactionSequence()));
        } else {
            this.setTransactionId(TransactionIdUtils.formatByteBuffer(null, traceId.getAgentStartTime(), traceId.getTransactionSequence()));
        }

        this.setSpanId(traceId.getSpanId());
        final long parentSpanId = traceId.getParentSpanId();
        if (traceId.getParentSpanId() != SpanId.NULL) {
            this.setParentSpanId(parentSpanId);
        }
        this.setFlag(traceId.getFlags());
    }

    private void setFlag(short flags) {
		// TODO Auto-generated method stub
		
	}

	private void setParentSpanId(long parentSpanId) {
		// TODO Auto-generated method stub
		
	}

	private void setSpanId(long spanId) {
		// TODO Auto-generated method stub
		
	}

	private void setTransactionId(ByteBuffer formatByteBuffer) {
		// TODO Auto-generated method stub
		
	}

	private String getAgentId() {
		// TODO Auto-generated method stub
		return null;
	}

	public void markBeforeTime() {
        this.setStartTime(System.currentTimeMillis());
    }

    private void setStartTime(long currentTimeMillis) {
		// TODO Auto-generated method stub
		
	}

	public void markAfterTime() {
        final int after = (int)(System.currentTimeMillis() - this.getStartTime());

        // TODO  have to change int to long
        if (after != 0) {
            this.setElapsed(after);
        }
    }

    private long getStartTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	private void setElapsed(int after) {
		// TODO Auto-generated method stub
		
	}

	public long getAfterTime() {
        return this.getStartTime() + this.getElapsed();
    }


    private long getElapsed() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void addAnnotation(Annotation annotation) {
        this.addToAnnotations(annotation);
    }

    public void setExceptionInfo(int exceptionClassId, String exceptionMessage) {
        final TIntStringValue exceptionInfo = new TIntStringValue(exceptionClassId);
        if (exceptionMessage != null && !exceptionMessage.isEmpty()) {
            exceptionInfo.setStringValue(exceptionMessage);
        }
        super.setExceptionInfo(exceptionInfo);
    }

    public boolean isSetErrCode() {
        return isSetErr();
    }

    public int getErrCode() {
        return getErr();
    }

    public void setErrCode(int exception) {
        super.setErr(exception);
    }

    public boolean isTimeRecording() {
        return timeRecording;
    }

    public void setTimeRecording(boolean timeRecording) {
        this.timeRecording = timeRecording;
    }

    @Override
    public Object attachFrameObject(Object attachObject) {
        final Object before = this.frameObject;
        this.frameObject = attachObject;
        return before;
    }

    @Override
    public Object getFrameObject() {
        return this.frameObject;
    }

    @Override
    public Object detachFrameObject() {
        final Object delete = this.frameObject;
        this.frameObject = null;
        return delete;
    }
}