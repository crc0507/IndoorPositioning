//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.oned.rss.expanded.decoders;

import com.google.zxing.common.BitArray;

abstract class AI01weightDecoder extends AI01decoder {
    AI01weightDecoder(BitArray information) {
        super(information);
    }

    protected void encodeCompressedWeight(StringBuffer buf, int currentPos, int weightSize) {
        int originalWeightNumeric = this.generalDecoder.extractNumericValueFromBitArray(currentPos, weightSize);
        this.addWeightCode(buf, originalWeightNumeric);
        int weightNumeric = this.checkWeight(originalWeightNumeric);
        int currentDivisor = 100000;

        for(int i = 0; i < 5; ++i) {
            if(weightNumeric / currentDivisor == 0) {
                buf.append('0');
            }

            currentDivisor /= 10;
        }

        buf.append(weightNumeric);
    }

    protected abstract void addWeightCode(StringBuffer var1, int var2);

    protected abstract int checkWeight(int var1);
}
