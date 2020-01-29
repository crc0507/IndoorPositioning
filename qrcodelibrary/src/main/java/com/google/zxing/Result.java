//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing;

import java.util.Enumeration;
import java.util.Hashtable;

public final class Result {
    private final String text;
    private final byte[] rawBytes;
    private ResultPoint[] resultPoints;
    private final BarcodeFormat format;
    private Hashtable resultMetadata;
    private final long timestamp;

    public Result(String text, byte[] rawBytes, ResultPoint[] resultPoints, BarcodeFormat format) {
        this(text, rawBytes, resultPoints, format, System.currentTimeMillis());
    }

    public Result(String text, byte[] rawBytes, ResultPoint[] resultPoints, BarcodeFormat format, long timestamp) {
        if(text == null && rawBytes == null) {
            throw new IllegalArgumentException("Text and bytes are null");
        } else {
            this.text = text;
            this.rawBytes = rawBytes;
            this.resultPoints = resultPoints;
            this.format = format;
            this.resultMetadata = null;
            this.timestamp = timestamp;
        }
    }

    public String getText() {
        return this.text;
    }

    public byte[] getRawBytes() {
        return this.rawBytes;
    }

    public ResultPoint[] getResultPoints() {
        return this.resultPoints;
    }

    public BarcodeFormat getBarcodeFormat() {
        return this.format;
    }

    public Hashtable getResultMetadata() {
        return this.resultMetadata;
    }

    public void putMetadata(ResultMetadataType type, Object value) {
        if(this.resultMetadata == null) {
            this.resultMetadata = new Hashtable(3);
        }

        this.resultMetadata.put(type, value);
    }

    public void putAllMetadata(Hashtable metadata) {
        if(metadata != null) {
            if(this.resultMetadata == null) {
                this.resultMetadata = metadata;
            } else {
                Enumeration e = metadata.keys();

                while(e.hasMoreElements()) {
                    ResultMetadataType key = (ResultMetadataType)e.nextElement();
                    Object value = metadata.get(key);
                    this.resultMetadata.put(key, value);
                }
            }
        }

    }

    public void addResultPoints(ResultPoint[] newPoints) {
        if(this.resultPoints == null) {
            this.resultPoints = newPoints;
        } else if(newPoints != null && newPoints.length > 0) {
            ResultPoint[] allPoints = new ResultPoint[this.resultPoints.length + newPoints.length];
            System.arraycopy(this.resultPoints, 0, allPoints, 0, this.resultPoints.length);
            System.arraycopy(newPoints, 0, allPoints, this.resultPoints.length, newPoints.length);
            this.resultPoints = allPoints;
        }

    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public String toString() {
        return this.text == null?"[" + this.rawBytes.length + " bytes]":this.text;
    }
}
