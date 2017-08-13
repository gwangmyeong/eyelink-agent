package com.m2u.eyelink.collector.bo.stat;

public enum JvmGcType {
    UNKNOWN(0),
    SERIAL(1),
    PARALLEL(2),
    CMS(3),
    G1(4);

    private final int typeCode;

    JvmGcType(int typeCode) {
        this.typeCode = typeCode;
    }

    public int getTypeCode() {
        return this.typeCode;
    }

    public static JvmGcType getTypeByCode(int typeCode) {
        for (JvmGcType gcType : JvmGcType.values()) {
            if (gcType.typeCode == typeCode) {
                return gcType;
            }
        }
        return JvmGcType.UNKNOWN;
    }
}
