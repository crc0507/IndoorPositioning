//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing;

public final class FormatException extends ReaderException {
    private static final FormatException instance = new FormatException();

    private FormatException() {
    }

    public static FormatException getFormatInstance() {
        return instance;
    }
}
