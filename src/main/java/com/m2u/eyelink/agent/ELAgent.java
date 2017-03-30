package com.m2u.eyelink.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class ELAgent {
	public static void premain(String agentArgs, Instrumentation instrumentation) {
		System.out.println("#### Call EyeLink Java Agent!!!! ###");
		instrumentation.addTransformer(new ClassFileTransformer() {
			public byte[] transform(ClassLoader l, String name, Class c,
					ProtectionDomain d, byte[] b)
					throws IllegalClassFormatException {
				byte[] ret = null; 
				
				/* TODO do somthing and fill ret */
				return ret;
			}
		});
	}
}
