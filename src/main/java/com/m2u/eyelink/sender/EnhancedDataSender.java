package com.m2u.eyelink.sender;

public interface EnhancedDataSender extends DataSender {
    boolean request(TBase<?, ?> data);
    boolean request(TBase<?, ?> data, int retry);
    boolean request(TBase<?, ?> data, FutureListener<ResponseMessage> listener);

    boolean addReconnectEventListener(ELAgentClientReconnectEventListener eventListener);
    boolean removeReconnectEventListener(ELAgentClientReconnectEventListener eventListener);

}
