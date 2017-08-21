package com.m2u.eyelink.collector.bo.codec.strategy;

import java.util.List;

public interface StrategyAnalyzer<T> {

    EncodingStrategy<T> getBestStrategy();

    List<T> getValues();

    interface StrategyAnalyzerBuilder<T> {

        StrategyAnalyzerBuilder<T> addValue(T value);

        StrategyAnalyzer<T> build();
    }
}
