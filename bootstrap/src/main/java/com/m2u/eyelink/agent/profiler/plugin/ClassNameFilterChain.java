package com.m2u.eyelink.agent.profiler.plugin;

import java.util.ArrayList;
import java.util.List;

public class ClassNameFilterChain implements ClassNameFilter {

    private final List<ClassNameFilter> filterChain;

    public ClassNameFilterChain(List<ClassNameFilter> filterChain) {
        if (filterChain == null) {
            throw new NullPointerException("filterChain must not be null");
        }
        this.filterChain = new ArrayList<ClassNameFilter>(filterChain);
    }


    @Override
    public boolean accept(String className) {
        for (ClassNameFilter classNameFilter : this.filterChain) {
            if (!classNameFilter.accept(className)) {
                return REJECT;
            }
        }
        return ACCEPT;
    }
}
