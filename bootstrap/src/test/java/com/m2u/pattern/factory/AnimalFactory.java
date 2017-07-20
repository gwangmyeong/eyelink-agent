package com.m2u.pattern.factory;

public class AnimalFactory {
	public static Animal create(String animalName) {
		if (animalName == null) {
			throw new IllegalArgumentException("null not allowed!!");
		}
		
		if (animalName.equals("소")) {
			return new Cow();
		} else if (animalName.equals("고양이")) {
			return new Cat();
		} else if (animalName.equals("개")) {
			return new Dog();
		} else {
			return null;
		}
		
	}
}
