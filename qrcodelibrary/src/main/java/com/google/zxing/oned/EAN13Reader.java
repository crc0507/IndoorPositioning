//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.NotFoundException;
import com.google.zxing.common.BitArray;

public final class EAN13Reader extends UPCEANReader {
    static final int[] FIRST_DIGIT_ENCODINGS = new int[]{0, 11, 13, 14, 19, 25, 28, 21, 22, 26};
    private final int[] decodeMiddleCounters = new int[4];

    public EAN13Reader() {
    }

    protected int decodeMiddle(BitArray row, int[] startRange, StringBuffer resultString) throws NotFoundException {
        int[] counters = this.decodeMiddleCounters;
        counters[0] = 0;
        counters[1] = 0;
        counters[2] = 0;
        counters[3] = 0;
        int end = row.getSize();
        int rowOffset = startRange[1];
        int lgPatternFound = 0;

        int x;
        int bestMatch;
        for(int middleRange = 0; middleRange < 6 && rowOffset < end; ++middleRange) {
            x = decodeDigit(row, counters, rowOffset, L_AND_G_PATTERNS);
            resultString.append((char)(48 + x % 10));

            for(bestMatch = 0; bestMatch < counters.length; ++bestMatch) {
                rowOffset += counters[bestMatch];
            }

            if(x >= 10) {
                lgPatternFound |= 1 << 5 - middleRange;
            }
        }

        determineFirstDigit(resultString, lgPatternFound);
        int[] var12 = findGuardPattern(row, rowOffset, true, MIDDLE_PATTERN);
        rowOffset = var12[1];

        for(x = 0; x < 6 && rowOffset < end; ++x) {
            bestMatch = decodeDigit(row, counters, rowOffset, L_PATTERNS);
            resultString.append((char)(48 + bestMatch));

            for(int i = 0; i < counters.length; ++i) {
                rowOffset += counters[i];
            }
        }

        return rowOffset;
    }

    BarcodeFormat getBarcodeFormat() {
        return BarcodeFormat.EAN_13;
    }

    private static void determineFirstDigit(StringBuffer resultString, int lgPatternFound) throws NotFoundException {
        for(int d = 0; d < 10; ++d) {
            if(lgPatternFound == FIRST_DIGIT_ENCODINGS[d]) {
                resultString.insert(0, (char)(48 + d));
                return;
            }
        }

        throw NotFoundException.getNotFoundInstance();
    }
}
