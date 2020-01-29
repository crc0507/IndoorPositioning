//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitArray;

import java.util.Hashtable;

public final class Code39Reader extends OneDReader {
    static final String ALPHABET_STRING = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. *$/+%";
    private static final char[] ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. *$/+%".toCharArray();
    static final int[] CHARACTER_ENCODINGS = new int[]{52, 289, 97, 352, 49, 304, 112, 37, 292, 100, 265, 73, 328, 25, 280, 88, 13, 268, 76, 28, 259, 67, 322, 19, 274, 82, 7, 262, 70, 22, 385, 193, 448, 145, 400, 208, 133, 388, 196, 148, 168, 162, 138, 42};
    private static final int ASTERISK_ENCODING;
    private final boolean usingCheckDigit;
    private final boolean extendedMode;

    public Code39Reader() {
        this.usingCheckDigit = false;
        this.extendedMode = false;
    }

    public Code39Reader(boolean usingCheckDigit) {
        this.usingCheckDigit = usingCheckDigit;
        this.extendedMode = false;
    }

    public Code39Reader(boolean usingCheckDigit, boolean extendedMode) {
        this.usingCheckDigit = usingCheckDigit;
        this.extendedMode = extendedMode;
    }

    public Result decodeRow(int rowNumber, BitArray row, Hashtable hints) throws NotFoundException, ChecksumException, FormatException {
        int[] start = findAsteriskPattern(row);
        int nextStart = start[1];

        int end;
        for(end = row.getSize(); nextStart < end && !row.get(nextStart); ++nextStart) {
            ;
        }

        StringBuffer result = new StringBuffer(20);
        int[] counters = new int[9];

        char decodedChar;
        int lastStart;
        int lastPatternSize;
        int whiteSpaceAfterEnd;
        do {
            recordPattern(row, nextStart, counters);
            lastPatternSize = toNarrowWidePattern(counters);
            if(lastPatternSize < 0) {
                throw NotFoundException.getNotFoundInstance();
            }

            decodedChar = patternToChar(lastPatternSize);
            result.append(decodedChar);
            lastStart = nextStart;

            for(whiteSpaceAfterEnd = 0; whiteSpaceAfterEnd < counters.length; ++whiteSpaceAfterEnd) {
                nextStart += counters[whiteSpaceAfterEnd];
            }

            while(nextStart < end && !row.get(nextStart)) {
                ++nextStart;
            }
        } while(decodedChar != 42);

        result.deleteCharAt(result.length() - 1);
        lastPatternSize = 0;

        for(whiteSpaceAfterEnd = 0; whiteSpaceAfterEnd < counters.length; ++whiteSpaceAfterEnd) {
            lastPatternSize += counters[whiteSpaceAfterEnd];
        }

        whiteSpaceAfterEnd = nextStart - lastStart - lastPatternSize;
        if(nextStart != end && whiteSpaceAfterEnd / 2 < lastPatternSize) {
            throw NotFoundException.getNotFoundInstance();
        } else {
            if(this.usingCheckDigit) {
                int resultString = result.length() - 1;
                int left = 0;

                for(int right = 0; right < resultString; ++right) {
                    left += "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. *$/+%".indexOf(result.charAt(right));
                }

                if(result.charAt(resultString) != ALPHABET[left % 43]) {
                    throw ChecksumException.getChecksumInstance();
                }

                result.deleteCharAt(resultString);
            }

            if(result.length() == 0) {
                throw NotFoundException.getNotFoundInstance();
            } else {
                String var16;
                if(this.extendedMode) {
                    var16 = decodeExtended(result);
                } else {
                    var16 = result.toString();
                }

                float var17 = (float)(start[1] + start[0]) / 2.0F;
                float var18 = (float)(nextStart + lastStart) / 2.0F;
                return new Result(var16, (byte[])null, new ResultPoint[]{new ResultPoint(var17, (float)rowNumber), new ResultPoint(var18, (float)rowNumber)}, BarcodeFormat.CODE_39);
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
        int[] counters = new int[9];
        int patternStart = rowOffset;
        boolean isWhite = false;
        int patternLength = counters.length;

        for(int i = rowOffset; i < width; ++i) {
            boolean pixel = row.get(i);
            if(pixel ^ isWhite) {
                ++counters[counterPosition];
            } else {
                if(counterPosition == patternLength - 1) {
                    if(toNarrowWidePattern(counters) == ASTERISK_ENCODING && row.isRange(Math.max(0, patternStart - (i - patternStart) / 2), patternStart, false)) {
                        return new int[]{patternStart, i};
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

    private static int toNarrowWidePattern(int[] counters) {
        int numCounters = counters.length;
        int maxNarrowCounter = 0;

        int wideCounters;
        do {
            int minCounter = 2147483647;

            int totalWideCountersWidth;
            int pattern;
            for(totalWideCountersWidth = 0; totalWideCountersWidth < numCounters; ++totalWideCountersWidth) {
                pattern = counters[totalWideCountersWidth];
                if(pattern < minCounter && pattern > maxNarrowCounter) {
                    minCounter = pattern;
                }
            }

            maxNarrowCounter = minCounter;
            wideCounters = 0;
            totalWideCountersWidth = 0;
            pattern = 0;

            int i;
            int counter;
            for(i = 0; i < numCounters; ++i) {
                counter = counters[i];
                if(counters[i] > maxNarrowCounter) {
                    pattern |= 1 << numCounters - 1 - i;
                    ++wideCounters;
                    totalWideCountersWidth += counter;
                }
            }

            if(wideCounters == 3) {
                for(i = 0; i < numCounters && wideCounters > 0; ++i) {
                    counter = counters[i];
                    if(counters[i] > maxNarrowCounter) {
                        --wideCounters;
                        if(counter << 1 >= totalWideCountersWidth) {
                            return -1;
                        }
                    }
                }

                return pattern;
            }
        } while(wideCounters > 3);

        return -1;
    }

    private static char patternToChar(int pattern) throws NotFoundException {
        for(int i = 0; i < CHARACTER_ENCODINGS.length; ++i) {
            if(CHARACTER_ENCODINGS[i] == pattern) {
                return ALPHABET[i];
            }
        }

        throw NotFoundException.getNotFoundInstance();
    }

    private static String decodeExtended(StringBuffer encoded) throws FormatException {
        int length = encoded.length();
        StringBuffer decoded = new StringBuffer(length);

        for(int i = 0; i < length; ++i) {
            char c = encoded.charAt(i);
            if(c != 43 && c != 36 && c != 37 && c != 47) {
                decoded.append(c);
            } else {
                char next = encoded.charAt(i + 1);
                char decodedChar = 0;
                switch(c) {
                    case '$':
                        if(next < 65 || next > 90) {
                            throw FormatException.getFormatInstance();
                        }

                        decodedChar = (char)(next - 64);
                        break;
                    case '%':
                        if(next >= 65 && next <= 69) {
                            decodedChar = (char)(next - 38);
                        } else {
                            if(next < 70 || next > 87) {
                                throw FormatException.getFormatInstance();
                            }

                            decodedChar = (char)(next - 11);
                        }
                        break;
                    case '+':
                        if(next < 65 || next > 90) {
                            throw FormatException.getFormatInstance();
                        }

                        decodedChar = (char)(next + 32);
                        break;
                    case '/':
                        if(next >= 65 && next <= 79) {
                            decodedChar = (char)(next - 32);
                        } else {
                            if(next != 90) {
                                throw FormatException.getFormatInstance();
                            }

                            decodedChar = 58;
                        }
                }

                decoded.append(decodedChar);
                ++i;
            }
        }

        return decoded.toString();
    }

    static {
        ASTERISK_ENCODING = CHARACTER_ENCODINGS[39];
    }
}
