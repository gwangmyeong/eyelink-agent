package com.m2u.eyelink.util.jdk;

import java.util.Random;

public interface ThreadLocalRandomFactory {
	Random current();
}
