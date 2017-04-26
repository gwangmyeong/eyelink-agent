package com.m2u.eyelink.context;

import com.m2u.eyelink.util.APIUtils;

public class DefaultMethodDescriptor implements MethodDescriptor {
	private String className;

	private String methodName;

	private String[] parameterTypes;

	private String[] parameterVariableName;

	private String parameterDescriptor;

	private String apiDescriptor;

	private int lineNumber;

	private int apiId = 0;

	private String fullName;

	private int type = 0;

	public DefaultMethodDescriptor() {
	}

	public DefaultMethodDescriptor(String className, String methodName,
			String[] parameterTypes, String[] parameterVariableName) {
		this.className = className;
		this.methodName = methodName;
		this.parameterTypes = parameterTypes;
		this.parameterVariableName = parameterVariableName;
		this.parameterDescriptor = APIUtils
				.mergeParameterVariableNameDescription(parameterTypes,
						parameterVariableName);
		this.apiDescriptor = APIUtils.mergeApiDescriptor(className, methodName,
				parameterDescriptor);
	}

	@Override
	public String getMethodName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getClassName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getParameterTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getParameterVariableName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getParameterDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getLineNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getFullName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setApiId(int apiId) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getApiId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getApiDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return 0;
	}

}
