//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.oned.rss;

public class DataCharacter {
    private final int value;
    private final int checksumPortion;

    public DataCharacter(int value, int checksumPortion) {
        this.value = value;
        this.checksumPortion = checksumPortion;
    }

    public int getValue() {
        return this.value;
    }

    public int getChecksumPortion() {
        return this.checksumPortion;
    }
}
