//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.qrcode.decoder;

public final class ErrorCorrectionLevel {
    public static final ErrorCorrectionLevel L = new ErrorCorrectionLevel(0, 1, "L");
    public static final ErrorCorrectionLevel M = new ErrorCorrectionLevel(1, 0, "M");
    public static final ErrorCorrectionLevel Q = new ErrorCorrectionLevel(2, 3, "Q");
    public static final ErrorCorrectionLevel H = new ErrorCorrectionLevel(3, 2, "H");
    private static final ErrorCorrectionLevel[] FOR_BITS;
    private final int ordinal;
    private final int bits;
    private final String name;

    private ErrorCorrectionLevel(int ordinal, int bits, String name) {
        this.ordinal = ordinal;
        this.bits = bits;
        this.name = name;
    }

    public int ordinal() {
        return this.ordinal;
    }

    public int getBits() {
        return this.bits;
    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        return this.name;
    }

    public static ErrorCorrectionLevel forBits(int bits) {
        if(bits >= 0 && bits < FOR_BITS.length) {
            return FOR_BITS[bits];
        } else {
            throw new IllegalArgumentException();
        }
    }

    static {
        FOR_BITS = new ErrorCorrectionLevel[]{M, L, H, Q};
    }
}
