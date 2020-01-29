//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.common;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.util.Vector;

public final class DecoderResult {
    private final byte[] rawBytes;
    private final String text;
    private final Vector byteSegments;
    private final ErrorCorrectionLevel ecLevel;

    public DecoderResult(byte[] rawBytes, String text, Vector byteSegments, ErrorCorrectionLevel ecLevel) {
        if(rawBytes == null && text == null) {
            throw new IllegalArgumentException();
        } else {
            this.rawBytes = rawBytes;
            this.text = text;
            this.byteSegments = byteSegments;
            this.ecLevel = ecLevel;
        }
    }

    public byte[] getRawBytes() {
        return this.rawBytes;
    }

    public String getText() {
        return this.text;
    }

    public Vector getByteSegments() {
        return this.byteSegments;
    }

    public ErrorCorrectionLevel getECLevel() {
        return this.ecLevel;
    }
}
