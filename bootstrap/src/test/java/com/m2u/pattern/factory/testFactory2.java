package com.m2u.pattern.factory;

public class testFactory2 {
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Animal a1 = AnimalFactory2.create("com.m2u.pattern.factory.Cow");
		a1.printDescription();
		Animal a2 = AnimalFactory2.create("com.m2u.pattern.factory.Cat");
		a2.printDescription();
		Animal a3 = AnimalFactory2.create("com.m2u.pattern.factory.Dog2");
		a3.printDescription();
	}
}
