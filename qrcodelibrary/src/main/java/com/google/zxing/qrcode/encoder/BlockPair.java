//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.qrcode.encoder;

final class BlockPair {
    private final byte[] dataBytes;
    private final byte[] errorCorrectionBytes;

    BlockPair(byte[] data, byte[] errorCorrection) {
        this.dataBytes = data;
        this.errorCorrectionBytes = errorCorrection;
    }

    public byte[] getDataBytes() {
        return this.dataBytes;
    }

    public byte[] getErrorCorrectionBytes() {
        return this.errorCorrectionBytes;
    }
}
