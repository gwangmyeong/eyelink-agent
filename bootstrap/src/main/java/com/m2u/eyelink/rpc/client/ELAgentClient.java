package com.m2u.eyelink.rpc.client;

import com.m2u.eyelink.rpc.ELAgentSocket;
import com.m2u.eyelink.rpc.Future;
import com.m2u.eyelink.sender.StreamChannelContext;

public interface ELAgentClient extends ELAgentSocket {


    /*
        because reconnectEventListener's constructor contains Dummy and can't be access through setter,
        guarantee it is not null.
    */
    boolean addELAgentClientReconnectEventListener(ELAgentClientReconnectEventListener eventListener);

    boolean removeELAgentClientReconnectEventListener(ELAgentClientReconnectEventListener eventListener);

    void reconnectSocketHandler(ELAgentClientHandler pinpointClientHandler);

    void sendSync(byte[] bytes) ;

    Future sendAsync(byte[] bytes);

    StreamChannelContext findStreamChannel(int streamChannelId);

    /**
     * write ping packet on tcp channel
     * PinpointSocketException throws when writing fails.
     *
     */
    void sendPing();


    boolean isClosed();

    boolean isConnected();

}
