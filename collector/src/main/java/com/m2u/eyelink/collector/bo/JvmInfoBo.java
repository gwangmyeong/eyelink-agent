package com.m2u.eyelink.collector.bo;

import com.m2u.eyelink.util.AutomaticBuffer;
import com.m2u.eyelink.util.Buffer;
import com.m2u.eyelink.util.FixedBuffer;

public class JvmInfoBo {

    private final byte version;
    private String jvmVersion;
    private String gcTypeName;

    public JvmInfoBo(int version) {
        if (version < 0 || version > 255) {
            throw new IllegalArgumentException("version out of range (0~255)");
        }
        this.version = (byte) (version & 0xFF);
    }

    public JvmInfoBo(byte[] serializedJvmInfoBo) {
        final Buffer buffer = new FixedBuffer(serializedJvmInfoBo);
        this.version = buffer.readByte();
        int version = this.version & 0xFF;
        switch (version) {
            case 0:
                this.jvmVersion = buffer.readPrefixedString();
                this.gcTypeName = buffer.readPrefixedString();
                break;
            default:
                this.jvmVersion = "";
                this.gcTypeName = "";
                break;
        }
    }

    public int getVersion() {
        return version & 0xFF;
    }

    public String getJvmVersion() {
        return jvmVersion;
    }

    public void setJvmVersion(String jvmVersion) {
        this.jvmVersion = jvmVersion;
    }

    public String getGcTypeName() {
        return gcTypeName;
    }

    public void setGcTypeName(String gcTypeName) {
        this.gcTypeName = gcTypeName;
    }

    public byte[] writeValue() {
        final Buffer buffer = new AutomaticBuffer();
        buffer.putByte(this.version);
        int version = this.version & 0xFF;
        switch (version) {
            case 0:
                buffer.putPrefixedString(this.jvmVersion);
                buffer.putPrefixedString(this.gcTypeName);
                break;
            default:
                break;
        }
        return buffer.getBuffer();
    }

    @Override
    public String toString() {
        return "JvmInfoBo{" +
                "version=" + version +
                ", jvmVersion='" + jvmVersion + '\'' +
                ", gcTypeName='" + gcTypeName + '\'' +
                '}';
    }
}
