//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.NotFoundException;
import com.google.zxing.common.BitArray;

public final class EAN8Reader extends UPCEANReader {
    private final int[] decodeMiddleCounters = new int[4];

    public EAN8Reader() {
    }

    protected int decodeMiddle(BitArray row, int[] startRange, StringBuffer result) throws NotFoundException {
        int[] counters = this.decodeMiddleCounters;
        counters[0] = 0;
        counters[1] = 0;
        counters[2] = 0;
        counters[3] = 0;
        int end = row.getSize();
        int rowOffset = startRange[1];

        int x;
        int bestMatch;
        for(int middleRange = 0; middleRange < 4 && rowOffset < end; ++middleRange) {
            x = decodeDigit(row, counters, rowOffset, L_PATTERNS);
            result.append((char)(48 + x));

            for(bestMatch = 0; bestMatch < counters.length; ++bestMatch) {
                rowOffset += counters[bestMatch];
            }
        }

        int[] var11 = findGuardPattern(row, rowOffset, true, MIDDLE_PATTERN);
        rowOffset = var11[1];

        for(x = 0; x < 4 && rowOffset < end; ++x) {
            bestMatch = decodeDigit(row, counters, rowOffset, L_PATTERNS);
            result.append((char)(48 + bestMatch));

            for(int i = 0; i < counters.length; ++i) {
                rowOffset += counters[i];
            }
        }

        return rowOffset;
    }

    BarcodeFormat getBarcodeFormat() {
        return BarcodeFormat.EAN_8;
    }
}
