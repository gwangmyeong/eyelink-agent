package com.m2u.eyelink.collector.bo.codec;

public interface ArithmeticOperation<T extends Number> {
    T add(T a, T b);
    T diff(T a, T b);
    T xor(T a, T b);
    T zero();

    ArithmeticOperation<Short> SHORT_OPERATIONS = new ArithmeticOperation<Short>() {

        @Override
        public Short add(Short a, Short b) {
            return (short) (a.shortValue() + b.shortValue());
        }

        @Override
        public Short diff(Short a, Short b) {
            return (short) (a.shortValue() - b.shortValue());
        }

        @Override
        public Short xor(Short a, Short b) {
            return (short) (a.shortValue() ^ b.shortValue());
        }

        @Override
        public Short zero() {
            return 0;
        }
    };

    ArithmeticOperation<Integer> INTEGER_OPERATIONS = new ArithmeticOperation<Integer>() {

        @Override
        public Integer add(Integer a, Integer b) {
            return a.intValue() + b.intValue();
        }

        @Override
        public Integer diff(Integer a, Integer b) {
            return a.intValue() - b.intValue();
        }

        @Override
        public Integer xor(Integer a, Integer b) {
            return a.intValue() ^ b.intValue();
        }

        @Override
        public Integer zero() {
            return 0;
        }
    };

    ArithmeticOperation<Long> LONG_OPERATIONS = new ArithmeticOperation<Long>() {

        @Override
        public Long add(Long a, Long b) {
            return a.longValue() + b.longValue();
        }

        @Override
        public Long diff(Long a, Long b) {
            return a.longValue() - b.longValue();
        }

        @Override
        public Long xor(Long a, Long b) {
            return a.longValue() ^ b.longValue();
        }

        @Override
        public Long zero() {
            return 0L;
        }
    };
}
