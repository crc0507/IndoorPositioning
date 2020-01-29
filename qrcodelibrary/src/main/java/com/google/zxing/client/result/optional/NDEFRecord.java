//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.client.result.optional;

import com.google.zxing.client.result.optional.AbstractNDEFResultParser;

final class NDEFRecord {
    private static final int SUPPORTED_HEADER_MASK = 63;
    private static final int SUPPORTED_HEADER = 17;
    public static final String TEXT_WELL_KNOWN_TYPE = "T";
    public static final String URI_WELL_KNOWN_TYPE = "U";
    public static final String SMART_POSTER_WELL_KNOWN_TYPE = "Sp";
    public static final String ACTION_WELL_KNOWN_TYPE = "act";
    private final int header;
    private final String type;
    private final byte[] payload;
    private final int totalRecordLength;

    private NDEFRecord(int header, String type, byte[] payload, int totalRecordLength) {
        this.header = header;
        this.type = type;
        this.payload = payload;
        this.totalRecordLength = totalRecordLength;
    }

    static NDEFRecord readRecord(byte[] bytes, int offset) {
        int header = bytes[offset] & 255;
        if(((header ^ 17) & 63) != 0) {
            return null;
        } else {
            int typeLength = bytes[offset + 1] & 255;
            int payloadLength = bytes[offset + 2] & 255;
            String type = AbstractNDEFResultParser.bytesToString(bytes, offset + 3, typeLength, "US-ASCII");
            byte[] payload = new byte[payloadLength];
            System.arraycopy(bytes, offset + 3 + typeLength, payload, 0, payloadLength);
            return new NDEFRecord(header, type, payload, 3 + typeLength + payloadLength);
        }
    }

    boolean isMessageBegin() {
        return (this.header & 128) != 0;
    }

    boolean isMessageEnd() {
        return (this.header & 64) != 0;
    }

    String getType() {
        return this.type;
    }

    byte[] getPayload() {
        return this.payload;
    }

    int getTotalRecordLength() {
        return this.totalRecordLength;
    }
}
