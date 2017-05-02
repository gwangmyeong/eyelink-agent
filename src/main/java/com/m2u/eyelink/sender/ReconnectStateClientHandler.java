package com.m2u.eyelink.sender;

import java.net.SocketAddress;

import com.m2u.eyelink.sender.ConnectFuture.Result;

public class ReconnectStateClientHandler implements ELAgentClientHandler {

    private static final ConnectFuture failedConnectFuture = new ConnectFuture();
    static {
        failedConnectFuture.setResult(Result.FAIL);
    }

    private volatile SocketStateCode state = SocketStateCode.BEING_CONNECT;
    
    @Override
    public void setConnectSocketAddress(SocketAddress connectSocketAddress) {
    }

    @Override
    public void initReconnect() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ConnectFuture getConnectFuture() {
        return failedConnectFuture;
    }
    
    @Override
    public void setELAgentClient(ELAgentClient pinpointClient) {
    }

    @Override
    public void sendSync(byte[] bytes) {
        throw newReconnectException();
    }

    @Override
    public Future sendAsync(byte[] bytes) {
        return reconnectFailureFuture();
    }

    private DefaultFuture<ResponseMessage> reconnectFailureFuture() {
        DefaultFuture<ResponseMessage> reconnect = new DefaultFuture<ResponseMessage>();
        reconnect.setFailure(newReconnectException());
        return reconnect;
    }

    @Override
    public void close() {
        this.state = SocketStateCode.CLOSED_BY_CLIENT;
    }

    @Override
    public void send(byte[] bytes) {
    }

    private ELAgentSocketException newReconnectException() {
        return new ELAgentSocketException("reconnecting...");
    }

    @Override
    public Future<ResponseMessage> request(byte[] bytes) {
        return reconnectFailureFuture();
    }

    @Override
    public void response(int requestId, byte[] payload) {

    }

    @Override
    public ClientStreamChannelContext openStream(byte[] payload, ClientStreamChannelMessageListener clientStreamChannelMessageListener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ClientStreamChannelContext openStream(byte[] payload, ClientStreamChannelMessageListener messageListener, StreamChannelStateChangeEventHandler<ClientStreamChannel> stateChangeListener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public StreamChannelContext findStreamChannel(int streamChannelId) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void sendPing() {
    }

    @Override
    public SocketStateCode getCurrentStateCode() {
        return state;
    }
    
    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public boolean isSupportServerMode() {
        return false;
    }

    @Override
    public SocketAddress getRemoteAddress() {
        return null;
    }

    @Override
    public ClusterOption getLocalClusterOption() {
        return null;
    }

    @Override
    public ClusterOption getRemoteClusterOption() {
        return null;
    }


}
