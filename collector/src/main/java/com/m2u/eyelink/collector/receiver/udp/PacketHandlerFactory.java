package com.m2u.eyelink.collector.receiver.udp;

public interface PacketHandlerFactory<T> {
	PacketHandler<T> createPacketHandler();
}
