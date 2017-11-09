package com.m2u.eyelink.rpc.util;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.context.HandshakePropertyType;
import com.m2u.eyelink.rpc.ELAgentSocket;
import com.m2u.eyelink.rpc.Future;
import com.m2u.eyelink.rpc.LoggingStateChangeEventListener;
import com.m2u.eyelink.rpc.MessageListener;
import com.m2u.eyelink.rpc.RequestPacket;
import com.m2u.eyelink.rpc.ResponseMessage;
import com.m2u.eyelink.rpc.client.DefaultELAgentClientFactory;
import com.m2u.eyelink.rpc.client.ELAgentClient;
import com.m2u.eyelink.rpc.client.ELAgentClientFactory;
import com.m2u.eyelink.rpc.packet.HandshakeResponseCode;
import com.m2u.eyelink.rpc.packet.HandshakeResponseType;
import com.m2u.eyelink.rpc.packet.PingPacket;
import com.m2u.eyelink.rpc.packet.SendPacket;
import com.m2u.eyelink.rpc.server.ELAgentServer;
import com.m2u.eyelink.rpc.server.ELAgentServerAcceptor;
import com.m2u.eyelink.rpc.server.ServerMessageListener;

public final class ELAgentRPCTestUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(ELAgentRPCTestUtils.class);

    private ELAgentRPCTestUtils() {
    }


    public static ELAgentServerAcceptor createELAgentServerFactory(int bindPort) {
        return createELAgentServerFactory(bindPort, null);
    }
    
    public static ELAgentServerAcceptor createELAgentServerFactory(int bindPort, ServerMessageListener messageListener) {
    	ELAgentServerAcceptor serverAcceptor = new ELAgentServerAcceptor();
        serverAcceptor.bind("127.0.0.1", bindPort);
        
        if (messageListener != null) {
            serverAcceptor.setMessageListener(messageListener);
        }

        return serverAcceptor;
    }
    
    public static void close(ELAgentServerAcceptor serverAcceptor, ELAgentServerAcceptor... serverAcceptors) {
        if (serverAcceptor != null) {
            serverAcceptor.close();
        }
        
        if (serverAcceptors != null) {
            for (ELAgentServerAcceptor eachServerAcceptor : serverAcceptors) {
                if (eachServerAcceptor != null) {
                    eachServerAcceptor.close();
                }
            }
        }
    }
    
    public static ELAgentClientFactory createClientFactory(Map<String, Object> param) {
        return createClientFactory(param, null);
    }
    
    public static ELAgentClientFactory createClientFactory(Map<String, Object> param, MessageListener messageListener) {
    	ELAgentClientFactory clientFactory = new DefaultELAgentClientFactory();
        clientFactory.setProperties(param);
        clientFactory.addStateChangeEventListener(LoggingStateChangeEventListener.INSTANCE);

        if (messageListener != null) {
            clientFactory.setMessageListener(messageListener);
        }
        
        return clientFactory;
    }

    public static byte[] request(ELAgentSocket writableServer, byte[] message) {
        Future<ResponseMessage> future = writableServer.request(message);
        future.await();
        return future.getResult().getMessage();
    }

    public static byte[] request(ELAgentClient client, byte[] message) {
        Future<ResponseMessage> future = client.request(message);
        future.await();
        return future.getResult().getMessage();
    }

    public static void close(ELAgentClient client, ELAgentClient... clients) {
        if (client != null) {
            client.close();
        }
        
        if (clients != null) {
            for (ELAgentClient eachSocket : clients) {
                if (eachSocket != null) {
                    eachSocket.close();
                }
            }
        }
    }
    
    public static void close(Socket socket, Socket... sockets) throws IOException {
        if (socket != null) {
            socket.close();
        }
        
        if (sockets != null) {
            for (Socket eachSocket : sockets) {
                if (eachSocket != null) {
                    eachSocket.close();
                }
            }
        }
    }

    
    public static EchoServerListener createEchoServerListener() {
        return new EchoServerListener();
    }

    public static EchoClientListener createEchoClientListener() {
        return new EchoClientListener();
    }

    public static Map<String, Object> getParams() {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(HandshakePropertyType.AGENT_ID.getName(), "agent");
        properties.put(HandshakePropertyType.APPLICATION_NAME.getName(), "application");
        properties.put(HandshakePropertyType.HOSTNAME.getName(), "hostname");
        properties.put(HandshakePropertyType.IP.getName(), "ip");
        properties.put(HandshakePropertyType.PID.getName(), 1111);
        properties.put(HandshakePropertyType.SERVICE_TYPE.getName(), 10);
        properties.put(HandshakePropertyType.START_TIMESTAMP.getName(), System.currentTimeMillis());
        properties.put(HandshakePropertyType.VERSION.getName(), "1.0");

        return properties;
    }

    public static class EchoServerListener implements ServerMessageListener {
        private final List<SendPacket> sendPacketRepository = new ArrayList<SendPacket>();
        private final List<RequestPacket> requestPacketRepository = new ArrayList<RequestPacket>();

        @Override
        public void handleSend(SendPacket sendPacket, ELAgentSocket elagentSocket) {
            logger.debug("handleSend packet:{}, remote:{}", sendPacket, elagentSocket.getRemoteAddress());
            sendPacketRepository.add(sendPacket);
        }

        @Override
        public void handleRequest(RequestPacket requestPacket, ELAgentSocket elagentSocket) {
            logger.debug("handleRequest packet:{}, remote:{}", requestPacket, elagentSocket.getRemoteAddress());

            requestPacketRepository.add(requestPacket);
            elagentSocket.response(requestPacket, requestPacket.getPayload());
        }

        @Override
        public HandshakeResponseCode handleHandshake(Map properties) {
            logger.debug("handle Handshake {}", properties);
            return HandshakeResponseType.Success.DUPLEX_COMMUNICATION;
        }

        @Override
        public void handlePing(PingPacket pingPacket, ELAgentServer elagentServer) {
            
        }
    }
    
    public static class EchoClientListener implements MessageListener {
        private final List<SendPacket> sendPacketRepository = new ArrayList<SendPacket>();
        private final List<RequestPacket> requestPacketRepository = new ArrayList<RequestPacket>();

        @Override
        public void handleSend(SendPacket sendPacket, ELAgentSocket elagentSocket) {
            sendPacketRepository.add(sendPacket);

            byte[] payload = sendPacket.getPayload();
            logger.debug(new String(payload));
        }

        @Override
        public void handleRequest(RequestPacket requestPacket, ELAgentSocket elagentSocket) {
            requestPacketRepository.add(requestPacket);

            byte[] payload = requestPacket.getPayload();
            logger.debug(new String(payload));

            elagentSocket.response(requestPacket, payload);
        }

        public List<SendPacket> getSendPacketRepository() {
            return sendPacketRepository;
        }

        public List<RequestPacket> getRequestPacketRepository() {
            return requestPacketRepository;
        }
    }

}
