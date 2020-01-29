//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing;

import com.google.zxing.common.BitArray;
import com.google.zxing.common.BitMatrix;

public abstract class Binarizer {
    private final LuminanceSource source;

    protected Binarizer(LuminanceSource source) {
        if(source == null) {
            throw new IllegalArgumentException("Source must be non-null.");
        } else {
            this.source = source;
        }
    }

    public LuminanceSource getLuminanceSource() {
        return this.source;
    }

    public abstract BitArray getBlackRow(int var1, BitArray var2) throws NotFoundException;

    public abstract BitMatrix getBlackMatrix() throws NotFoundException;

    public abstract Binarizer createBinarizer(LuminanceSource var1);
}
