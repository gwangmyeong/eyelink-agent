package com.m2u.eyelink.collector.receiver.udp;

import java.net.DatagramSocket;

public interface PacketHandler<T> {
	void receive(DatagramSocket localSocket, T packet);
}
