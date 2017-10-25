package com.m2u.eyelink.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.rpc.client.DefaultELAgentClientFactory;
import com.m2u.eyelink.rpc.client.ELAgentClient;
import com.m2u.eyelink.rpc.client.ELAgentClientFactory;
import com.m2u.eyelink.rpc.server.ELAgentServerAcceptor;

public final class ClassPreLoader {

    public static void preload() {
        try {
        	// TODO pinpoint 와 동시실행으로 인한 충돌 방지 처리 
            preload(65534);
        } catch (Exception ignore) {
            // skip
        }
    }

    public static void preload(int port) {
        ELAgentServerAcceptor serverAcceptor = null;
        ELAgentClient client = null;
        ELAgentClientFactory clientFactory = null;
        try {
            serverAcceptor = new ELAgentServerAcceptor();
            serverAcceptor.bind("127.0.0.1", port);

            clientFactory = new DefaultELAgentClientFactory();
            client = clientFactory.connect("127.0.0.1", port);
            client.sendSync(new byte[0]);


        } catch (Exception ex) {

            System.err.print("preLoad error Caused:" + ex.getMessage());
            ex.printStackTrace();

            final Logger logger = LoggerFactory.getLogger(ClassPreLoader.class);
            logger.warn("preLoad error Caused:{}", ex.getMessage(), ex);
            if (ex instanceof ELAgentSocketException) {
                throw (ELAgentSocketException)ex;
            } else {
                throw new ELAgentSocketException(ex.getMessage(), ex);
            }
        } finally {
            if (client != null) {
                try {
                    client.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if(clientFactory != null) {
                try {
                    clientFactory.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (serverAcceptor != null) {
                try {
                    serverAcceptor.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
