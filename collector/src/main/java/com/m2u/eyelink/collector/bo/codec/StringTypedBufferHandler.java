package com.m2u.eyelink.collector.bo.codec;

import com.m2u.eyelink.util.Buffer;

public interface StringTypedBufferHandler {
    void put(Buffer buffer, String value);
    String read(Buffer buffer);

    StringTypedBufferHandler VARIABLE_HANDLER = new StringTypedBufferHandler() {

        @Override
        public void put(Buffer buffer, String value) {
            buffer.putPrefixedString(value);
        }

        @Override
        public String read(Buffer buffer) {
            return buffer.readPrefixedString();
        }

    };

}
