package com.m2u.eyelink.collector.handler;

import org.apache.thrift.TBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DualAgentStatHandler implements Handler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Handler master;
    private final Handler slave;

    DualAgentStatHandler(Handler master, Handler slave) {
        if (master == null) {
            throw new NullPointerException("master must not be null");
        }
        if (slave == null) {
            throw new NullPointerException("slave must not be null");
        }
        this.master = master;
        this.slave = slave;
    }

    @Override
    public void handle(TBase<?, ?> tbase) {
        Throwable masterException = null;
        try {
            master.handle(tbase);
        } catch (Throwable t) {
            masterException = t;
        }
        try {
            slave.handle(tbase);
        } catch (Throwable t) {
            logger.warn("slave handle({}) Error:{}", tbase.getClass().getSimpleName(), t.getMessage(), t);
        }
        if (masterException != null) {
            throw new RuntimeException(masterException);
        }
    }
}
