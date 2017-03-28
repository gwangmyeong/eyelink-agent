package com.m2u.eyelink.agent;

import java.lang.instrument.Instrumentation;

public class ELAgent {
	public static void premain(String agentArgs, Instrumentation instrumentation) {
		System.out.println("#### Hello EyeLink Java Agent!!!! ###");
	}
}
