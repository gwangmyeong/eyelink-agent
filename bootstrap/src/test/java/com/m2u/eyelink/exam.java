package com.m2u.eyelink;

import org.junit.Test;

public class exam {

	@Test
	public void ExamTest() {
		String str1 = "MyName";
		String str2 = "MyName";
		String str3 = new String(str2);

		System.out.println(str1 == str2);
		System.out.println(str1 == str3);
		System.out.println(str1.equals(str2));
		System.out.println(str1.equals(str3));		
	}
	
	@Test
	public void Exam2Test() {
		String str12 = "MyName";
		String str13 = str12;
		System.out.println(str12 == str13);
		System.out.println(str12.equals(str13));		
	}
}
