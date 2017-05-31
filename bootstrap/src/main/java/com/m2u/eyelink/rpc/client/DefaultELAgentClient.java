package com.m2u.eyelink.rpc.client;

import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.rpc.ClientStreamChannelContext;
import com.m2u.eyelink.rpc.ClientStreamChannelMessageListener;
import com.m2u.eyelink.rpc.ClusterOption;
import com.m2u.eyelink.rpc.ELAgentSocketException;
import com.m2u.eyelink.rpc.Future;
import com.m2u.eyelink.rpc.RequestPacket;
import com.m2u.eyelink.rpc.ResponseMessage;
import com.m2u.eyelink.rpc.StreamChannelStateChangeEventHandler;
import com.m2u.eyelink.rpc.stream.ClientStreamChannel;
import com.m2u.eyelink.sender.DefaultFuture;
import com.m2u.eyelink.sender.StreamChannelContext;
import com.m2u.eyelink.util.AssertUtils;

public class DefaultELAgentClient implements ELAgentClient {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private volatile ELAgentClientHandler pinpointClientHandler;

    private volatile boolean closed;

    private List<ELAgentClientReconnectEventListener> reconnectEventListeners = new CopyOnWriteArrayList<ELAgentClientReconnectEventListener>();

    public DefaultELAgentClient() {
        this(new ReconnectStateClientHandler());
    }

    public DefaultELAgentClient(ELAgentClientHandler pinpointClientHandler) {
        AssertUtils.assertNotNull(pinpointClientHandler, "pinpointClientHandler");

        this.pinpointClientHandler = pinpointClientHandler;
        pinpointClientHandler.setELAgentClient(this);
    }

    @Override
    public void reconnectSocketHandler(ELAgentClientHandler pinpointClientHandler) {
        AssertUtils.assertNotNull(pinpointClientHandler, "pinpointClientHandler");

        if (closed) {
            logger.warn("reconnectClientHandler(). pinpointClientHandler force close.");
            pinpointClientHandler.close();
            return;
        }
        logger.warn("reconnectClientHandler:{}", pinpointClientHandler);

        this.pinpointClientHandler = pinpointClientHandler;

        notifyReconnectEvent();
    }


    /*
        because reconnectEventListener's constructor contains Dummy and can't be access through setter,
        guarantee it is not null.
    */
    @Override
    public boolean addELAgentClientReconnectEventListener(ELAgentClientReconnectEventListener eventListener) {
        if (eventListener == null) {
            return false;
        }

        return this.reconnectEventListeners.add(eventListener);
    }

    @Override
    public boolean removeELAgentClientReconnectEventListener(ELAgentClientReconnectEventListener eventListener) {
        if (eventListener == null) {
            return false;
        }

        return this.reconnectEventListeners.remove(eventListener);
    }

    private void notifyReconnectEvent() {
        for (ELAgentClientReconnectEventListener eachListener : this.reconnectEventListeners) {
            eachListener.reconnectPerformed(this);
        }
    }

    @Override
    public void sendSync(byte[] bytes) {
        ensureOpen();
        pinpointClientHandler.sendSync(bytes);
    }

    @Override
    public Future sendAsync(byte[] bytes) {
        ensureOpen();
        return pinpointClientHandler.sendAsync(bytes);
    }

    @Override
    public void send(byte[] bytes) {
        ensureOpen();
        pinpointClientHandler.send(bytes);
    }

    @Override
    public Future<ResponseMessage> request(byte[] bytes) {
        if (pinpointClientHandler == null) {
            return returnFailureFuture();
        }
        return pinpointClientHandler.request(bytes);
    }

    @Override
    public void response(RequestPacket requestPacket, byte[] payload) {
        response(requestPacket.getRequestId(), payload);
    }

    @Override
    public void response(int requestId, byte[] payload) {
        ensureOpen();
        pinpointClientHandler.response(requestId, payload);
    }

    @Override
    public ClientStreamChannelContext openStream(byte[] payload, ClientStreamChannelMessageListener messageListener) {
        return openStream(payload, messageListener, null);
    }

    @Override
    public ClientStreamChannelContext openStream(byte[] payload, ClientStreamChannelMessageListener messageListener, StreamChannelStateChangeEventHandler<ClientStreamChannel> stateChangeListener) {
        // StreamChannel must be changed into interface in order to throw the StreamChannel that returns failure.
        // fow now throw just exception
        ensureOpen();
        return pinpointClientHandler.openStream(payload, messageListener, stateChangeListener);
    }

    @Override
    public SocketAddress getRemoteAddress() {
        return pinpointClientHandler.getRemoteAddress();
    }

    @Override
    public ClusterOption getLocalClusterOption() {
        return pinpointClientHandler.getLocalClusterOption();
    }

    @Override
    public ClusterOption getRemoteClusterOption() {
        return pinpointClientHandler.getRemoteClusterOption();
    }

    @Override
    public StreamChannelContext findStreamChannel(int streamChannelId) {

        ensureOpen();
        return pinpointClientHandler.findStreamChannel(streamChannelId);
    }

    private Future<ResponseMessage> returnFailureFuture() {
        DefaultFuture<ResponseMessage> future = new DefaultFuture<ResponseMessage>();
        future.setFailure(new ELAgentSocketException("pinpointClientHandler is null"));
        return future;
    }

    private void ensureOpen() {
        if (pinpointClientHandler == null) {
            throw new ELAgentSocketException("pinpointClientHandler is null");
        }
    }

    /**
     * write ping packet on tcp channel
     * PinpointSocketException throws when writing fails.
     *
     */
    @Override
    public void sendPing() {
    	ELAgentClientHandler pinpointClientHandler = this.pinpointClientHandler;
        if (pinpointClientHandler == null) {
            return;
        }
        pinpointClientHandler.sendPing();
    }

    @Override
    public void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
            closed = true;
        }
        ELAgentClientHandler pinpointClientHandler = this.pinpointClientHandler;
        if (pinpointClientHandler == null) {
            return;
        }
        pinpointClientHandler.close();
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public boolean isConnected() {
        return this.pinpointClientHandler.isConnected();
    }
}
