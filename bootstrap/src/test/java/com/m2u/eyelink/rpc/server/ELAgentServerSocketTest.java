package com.m2u.eyelink.rpc.server;

import java.io.IOException;
import java.net.Socket;

import org.junit.BeforeClass;
import org.junit.Test;

import com.m2u.eyelink.rpc.DiscardPipelineFactory;
import com.m2u.eyelink.rpc.util.ELAgentRPCTestUtils;
import com.m2u.eyelink.rpc.util.SocketUtils;

public class ELAgentServerSocketTest {
    
    private static int bindPort;
    
    @BeforeClass
    public static void setUp() throws IOException {
        bindPort = SocketUtils.findAvailableTcpPort();
    }
    
    @Test
    public void testBind() throws Exception {
        ELAgentServerAcceptor serverAcceptor = new ELAgentServerAcceptor();
        serverAcceptor.setPipelineFactory(new DiscardPipelineFactory());
        serverAcceptor.bind("127.0.0.1", bindPort);

        Socket socket = new Socket("127.0.0.1", bindPort);
        socket.getOutputStream().write(new byte[10]);
        socket.getOutputStream().flush();
        socket.close();

        Thread.sleep(1000);
        ELAgentRPCTestUtils.close(serverAcceptor);
    }


}
