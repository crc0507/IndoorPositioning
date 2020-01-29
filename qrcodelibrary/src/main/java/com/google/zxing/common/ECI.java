//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.common;

import com.google.zxing.common.CharacterSetECI;

public abstract class ECI {
    private final int value;

    ECI(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static ECI getECIByValue(int value) {
        if(value >= 0 && value <= 999999) {
            return value < 900?CharacterSetECI.getCharacterSetECIByValue(value):null;
        } else {
            throw new IllegalArgumentException("Bad ECI value: " + value);
        }
    }
}
