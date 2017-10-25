package com.m2u.pattern.factory;

public class testFactory {
	public static void main(String[] args) {
		Animal a1 = AnimalFactory.create("소");
		a1.printDescription();
		Animal a2 = AnimalFactory.create("고양이");
		a2.printDescription();
		Animal a3 = AnimalFactory.create("개");
		a3.printDescription();
	}
}
