//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing;

public final class NotFoundException extends ReaderException {
    private static final NotFoundException instance = new NotFoundException();

    private NotFoundException() {
    }

    public static NotFoundException getNotFoundInstance() {
        return instance;
    }
}
