package com.m2u.eyelink.agent.sample;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

public class ELClassFileTransformer5 implements ClassFileTransformer {

	public ELClassFileTransformer5() {
		super();
	}
	
	@Override
	public byte[] transform(ClassLoader loader, String className,
			Class classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		
		System.out.println("className : " + className);
		
		// Method Filter 적
		ClassReader cr = new ClassReader(classfileBuffer); 
		ClassWriter cw = new ClassWriter(cr, 0); 
		RemoveMethodAdapter ca = new RemoveMethodAdapter(Opcodes.ASM5, cw, "getAaa()", "abc"); 
		cr.accept(ca, 0); 

			
		return cw.toByteArray(); // ret represents the same class as b
	}

}
