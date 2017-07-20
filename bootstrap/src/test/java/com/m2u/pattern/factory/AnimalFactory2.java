package com.m2u.pattern.factory;

public class AnimalFactory2 {
	public static Animal create(String animalName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class<?> clazz = Class.forName(animalName);
		Animal ret = (Animal)clazz.newInstance();
		return ret;
		
	}
}
