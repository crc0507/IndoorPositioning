//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitArray;

import java.util.Hashtable;

public final class ITFReader extends OneDReader {
    private static final int MAX_AVG_VARIANCE = 107;
    private static final int MAX_INDIVIDUAL_VARIANCE = 204;
    private static final int W = 3;
    private static final int N = 1;
    private static final int[] DEFAULT_ALLOWED_LENGTHS = new int[]{6, 10, 12, 14, 44};
    private int narrowLineWidth = -1;
    private static final int[] START_PATTERN = new int[]{1, 1, 1, 1};
    private static final int[] END_PATTERN_REVERSED = new int[]{1, 1, 3};
    static final int[][] PATTERNS = new int[][]{{1, 1, 3, 3, 1}, {3, 1, 1, 1, 3}, {1, 3, 1, 1, 3}, {3, 3, 1, 1, 1}, {1, 1, 3, 1, 3}, {3, 1, 3, 1, 1}, {1, 3, 3, 1, 1}, {1, 1, 1, 3, 3}, {3, 1, 1, 3, 1}, {1, 3, 1, 3, 1}};

    public ITFReader() {
    }

    public Result decodeRow(int rowNumber, BitArray row, Hashtable hints) throws FormatException, NotFoundException {
        int[] startRange = this.decodeStart(row);
        int[] endRange = this.decodeEnd(row);
        StringBuffer result = new StringBuffer(20);
        decodeMiddle(row, startRange[1], endRange[0], result);
        String resultString = result.toString();
        int[] allowedLengths = null;
        if(hints != null) {
            allowedLengths = (int[])((int[])hints.get(DecodeHintType.ALLOWED_LENGTHS));
        }

        if(allowedLengths == null) {
            allowedLengths = DEFAULT_ALLOWED_LENGTHS;
        }

        int length = resultString.length();
        boolean lengthOK = false;

        for(int i = 0; i < allowedLengths.length; ++i) {
            if(length == allowedLengths[i]) {
                lengthOK = true;
                break;
            }
        }

        if(!lengthOK) {
            throw FormatException.getFormatInstance();
        } else {
            return new Result(resultString, (byte[])null, new ResultPoint[]{new ResultPoint((float)startRange[1], (float)rowNumber), new ResultPoint((float)endRange[0], (float)rowNumber)}, BarcodeFormat.ITF);
        }
    }

    private static void decodeMiddle(BitArray row, int payloadStart, int payloadEnd, StringBuffer resultString) throws NotFoundException {
        int[] counterDigitPair = new int[10];
        int[] counterBlack = new int[5];
        int[] counterWhite = new int[5];

        while(payloadStart < payloadEnd) {
            recordPattern(row, payloadStart, counterDigitPair);

            int bestMatch;
            int i;
            for(bestMatch = 0; bestMatch < 5; ++bestMatch) {
                i = bestMatch << 1;
                counterBlack[bestMatch] = counterDigitPair[i];
                counterWhite[bestMatch] = counterDigitPair[i + 1];
            }

            bestMatch = decodeDigit(counterBlack);
            resultString.append((char)(48 + bestMatch));
            bestMatch = decodeDigit(counterWhite);
            resultString.append((char)(48 + bestMatch));

            for(i = 0; i < counterDigitPair.length; ++i) {
                payloadStart += counterDigitPair[i];
            }
        }

    }

    int[] decodeStart(BitArray row) throws NotFoundException {
        int endStart = skipWhiteSpace(row);
        int[] startPattern = findGuardPattern(row, endStart, START_PATTERN);
        this.narrowLineWidth = startPattern[1] - startPattern[0] >> 2;
        this.validateQuietZone(row, startPattern[0]);
        return startPattern;
    }

    private void validateQuietZone(BitArray row, int startPattern) throws NotFoundException {
        int quietCount = this.narrowLineWidth * 10;

        for(int i = startPattern - 1; quietCount > 0 && i >= 0 && !row.get(i); --i) {
            --quietCount;
        }

        if(quietCount != 0) {
            throw NotFoundException.getNotFoundInstance();
        }
    }

    private static int skipWhiteSpace(BitArray row) throws NotFoundException {
        int width = row.getSize();

        int endStart;
        for(endStart = 0; endStart < width && !row.get(endStart); ++endStart) {
            ;
        }

        if(endStart == width) {
            throw NotFoundException.getNotFoundInstance();
        } else {
            return endStart;
        }
    }

    int[] decodeEnd(BitArray row) throws NotFoundException {
        row.reverse();

        int[] var5;
        try {
            int endStart = skipWhiteSpace(row);
            int[] endPattern = findGuardPattern(row, endStart, END_PATTERN_REVERSED);
            this.validateQuietZone(row, endPattern[0]);
            int temp = endPattern[0];
            endPattern[0] = row.getSize() - endPattern[1];
            endPattern[1] = row.getSize() - temp;
            var5 = endPattern;
        } finally {
            row.reverse();
        }

        return var5;
    }

    private static int[] findGuardPattern(BitArray row, int rowOffset, int[] pattern) throws NotFoundException {
        int patternLength = pattern.length;
        int[] counters = new int[patternLength];
        int width = row.getSize();
        boolean isWhite = false;
        int counterPosition = 0;
        int patternStart = rowOffset;

        for(int x = rowOffset; x < width; ++x) {
            boolean pixel = row.get(x);
            if(pixel ^ isWhite) {
                ++counters[counterPosition];
            } else {
                if(counterPosition == patternLength - 1) {
                    if(patternMatchVariance(counters, pattern, 204) < 107) {
                        return new int[]{patternStart, x};
                    }

                    patternStart += counters[0] + counters[1];

                    for(int y = 2; y < patternLength; ++y) {
                        counters[y - 2] = counters[y];
                    }

                    counters[patternLength - 2] = 0;
                    counters[patternLength - 1] = 0;
                    --counterPosition;
                } else {
                    ++counterPosition;
                }

                counters[counterPosition] = 1;
                isWhite = !isWhite;
            }
        }

        throw NotFoundException.getNotFoundInstance();
    }

    private static int decodeDigit(int[] counters) throws NotFoundException {
        int bestVariance = 107;
        int bestMatch = -1;
        int max = PATTERNS.length;

        for(int i = 0; i < max; ++i) {
            int[] pattern = PATTERNS[i];
            int variance = patternMatchVariance(counters, pattern, 204);
            if(variance < bestVariance) {
                bestVariance = variance;
                bestMatch = i;
            }
        }

        if(bestMatch >= 0) {
            return bestMatch;
        } else {
            throw NotFoundException.getNotFoundInstance();
        }
    }
}
