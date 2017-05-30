package com.m2u.eyelink.util.jdk;

import java.util.Random;


public class ELAgentThreadLocalRandomFactory implements
		ThreadLocalRandomFactory {

    @Override
    public Random current() {
        return ThreadLocalRandom.current();
    }

}
