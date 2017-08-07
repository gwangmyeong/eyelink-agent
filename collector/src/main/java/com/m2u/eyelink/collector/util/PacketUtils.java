package com.m2u.eyelink.collector.util;

import java.net.DatagramPacket;

public final class PacketUtils {
    private PacketUtils() {
    }

    public static String dumpDatagramPacket(DatagramPacket datagramPacket) {
        if (datagramPacket == null) {
            return "null";
        }
        // FIXME bsh, 기존 bytes 는 Hbase class를 사용하고 있는데 이를 ES로 저장하기 위해서 로직 변경해야함.
//        return Bytes.toStringBinary(datagramPacket.getData(), 0, datagramPacket.getLength());
        return null;
    }

    public static String dumpByteArray(byte[] bytes) {
        if (bytes == null) {
            return "null";
        }
        // FIXME bsh, 기존 bytes 는 Hbase class를 사용하고 있는데 이를 ES로 저장하기 위해서 로직 변경해야함.
//        return Bytes.toStringBinary(bytes, 0, bytes.length);
        return null;
    }
}
