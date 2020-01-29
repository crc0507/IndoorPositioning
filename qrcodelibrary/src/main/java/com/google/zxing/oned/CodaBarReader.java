//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitArray;

import java.util.Hashtable;

public final class CodaBarReader extends OneDReader {
    private static final String ALPHABET_STRING = "0123456789-$:/.+ABCDTN";
    private static final char[] ALPHABET = "0123456789-$:/.+ABCDTN".toCharArray();
    private static final int[] CHARACTER_ENCODINGS = new int[]{3, 6, 9, 96, 18, 66, 33, 36, 48, 72, 12, 24, 37, 81, 84, 21, 26, 41, 11, 14, 26, 41};
    private static final int minCharacterLength = 6;
    private static final char[] STARTEND_ENCODING = new char[]{'E', '*', 'A', 'B', 'C', 'D', 'T', 'N'};

    public CodaBarReader() {
    }

    public Result decodeRow(int rowNumber, BitArray row, Hashtable hints) throws NotFoundException {
        int[] start = findAsteriskPattern(row);
        start[1] = 0;
        int nextStart = start[1];

        int end;
        for(end = row.getSize(); nextStart < end && !row.get(nextStart); ++nextStart) {
            ;
        }

        StringBuffer result = new StringBuffer();

        int[] counters;
        int lastStart;
        int whiteSpaceAfterEnd;
        do {
            counters = new int[]{0, 0, 0, 0, 0, 0, 0};
            recordPattern(row, nextStart, counters);
            char lastPatternSize = toNarrowWidePattern(counters);
            if(lastPatternSize == 33) {
                throw NotFoundException.getNotFoundInstance();
            }

            result.append(lastPatternSize);
            lastStart = nextStart;

            for(whiteSpaceAfterEnd = 0; whiteSpaceAfterEnd < counters.length; ++whiteSpaceAfterEnd) {
                nextStart += counters[whiteSpaceAfterEnd];
            }

            while(nextStart < end && !row.get(nextStart)) {
                ++nextStart;
            }
        } while(nextStart < end);

        int var15 = 0;

        for(whiteSpaceAfterEnd = 0; whiteSpaceAfterEnd < counters.length; ++whiteSpaceAfterEnd) {
            var15 += counters[whiteSpaceAfterEnd];
        }

        whiteSpaceAfterEnd = nextStart - lastStart - var15;
        if(nextStart != end && whiteSpaceAfterEnd / 2 < var15) {
            throw NotFoundException.getNotFoundInstance();
        } else if(result.length() < 2) {
            throw NotFoundException.getNotFoundInstance();
        } else {
            char startchar = result.charAt(0);
            if(!arrayContains(STARTEND_ENCODING, startchar)) {
                throw NotFoundException.getNotFoundInstance();
            } else {
                for(int left = 1; left < result.length(); ++left) {
                    if(result.charAt(left) == startchar && left + 1 != result.length()) {
                        result.delete(left + 1, result.length() - 1);
                        left = result.length();
                    }
                }

                if(result.length() > 6) {
                    result.deleteCharAt(result.length() - 1);
                    result.deleteCharAt(0);
                    float var16 = (float)(start[1] + start[0]) / 2.0F;
                    float right = (float)(nextStart + lastStart) / 2.0F;
                    return new Result(result.toString(), (byte[])null, new ResultPoint[]{new ResultPoint(var16, (float)rowNumber), new ResultPoint(right, (float)rowNumber)}, BarcodeFormat.CODABAR);
                } else {
                    throw NotFoundException.getNotFoundInstance();
                }
            }
        }
    }

    private static int[] findAsteriskPattern(BitArray row) throws NotFoundException {
        int width = row.getSize();

        int rowOffset;
        for(rowOffset = 0; rowOffset < width && !row.get(rowOffset); ++rowOffset) {
            ;
        }

        int counterPosition = 0;
        int[] counters = new int[7];
        int patternStart = rowOffset;
        boolean isWhite = false;
        int patternLength = counters.length;

        for(int i = rowOffset; i < width; ++i) {
            boolean pixel = row.get(i);
            if(pixel ^ isWhite) {
                ++counters[counterPosition];
            } else {
                if(counterPosition == patternLength - 1) {
                    try {
                        if(arrayContains(STARTEND_ENCODING, toNarrowWidePattern(counters)) && row.isRange(Math.max(0, patternStart - (i - patternStart) / 2), patternStart, false)) {
                            return new int[]{patternStart, i};
                        }
                    } catch (IllegalArgumentException var11) {
                        ;
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
                isWhite ^= true;
            }
        }

        throw NotFoundException.getNotFoundInstance();
    }

    private static boolean arrayContains(char[] array, char key) {
        if(array != null) {
            for(int i = 0; i < array.length; ++i) {
                if(array[i] == key) {
                    return true;
                }
            }
        }

        return false;
    }

    private static char toNarrowWidePattern(int[] counters) {
        int numCounters = counters.length;
        int maxNarrowCounter = 0;
        int minCounter = 2147483647;

        int wideCounters;
        for(wideCounters = 0; wideCounters < numCounters; ++wideCounters) {
            if(counters[wideCounters] < minCounter) {
                minCounter = counters[wideCounters];
            }

            if(counters[wideCounters] > maxNarrowCounter) {
                maxNarrowCounter = counters[wideCounters];
            }
        }

        do {
            wideCounters = 0;
            int pattern = 0;

            int i;
            for(i = 0; i < numCounters; ++i) {
                if(counters[i] > maxNarrowCounter) {
                    pattern |= 1 << numCounters - 1 - i;
                    ++wideCounters;
                }
            }

            if(wideCounters == 2 || wideCounters == 3) {
                for(i = 0; i < CHARACTER_ENCODINGS.length; ++i) {
                    if(CHARACTER_ENCODINGS[i] == pattern) {
                        return ALPHABET[i];
                    }
                }
            }

            --maxNarrowCounter;
        } while(maxNarrowCounter > minCounter);

        return '!';
    }
}
