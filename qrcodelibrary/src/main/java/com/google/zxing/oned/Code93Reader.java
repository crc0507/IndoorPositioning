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

public final class Code93Reader extends OneDReader {
    private static final String ALPHABET_STRING = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%abcd*";
    private static final char[] ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%abcd*".toCharArray();
    private static final int[] CHARACTER_ENCODINGS = new int[]{276, 328, 324, 322, 296, 292, 290, 336, 274, 266, 424, 420, 418, 404, 402, 394, 360, 356, 354, 308, 282, 344, 332, 326, 300, 278, 436, 434, 428, 422, 406, 410, 364, 358, 310, 314, 302, 468, 466, 458, 366, 374, 430, 294, 474, 470, 306, 350};
    private static final int ASTERISK_ENCODING;

    public Code93Reader() {
    }

    public Result decodeRow(int rowNumber, BitArray row, Hashtable hints) throws NotFoundException, ChecksumException, FormatException {
        int[] start = findAsteriskPattern(row);
        int nextStart = start[1];

        int end;
        for(end = row.getSize(); nextStart < end && !row.get(nextStart); ++nextStart) {
            ;
        }

        StringBuffer result = new StringBuffer(20);
        int[] counters = new int[6];

        char decodedChar;
        int lastStart;
        do {
            recordPattern(row, nextStart, counters);
            int resultString = toPattern(counters);
            if(resultString < 0) {
                throw NotFoundException.getNotFoundInstance();
            }

            decodedChar = patternToChar(resultString);
            result.append(decodedChar);
            lastStart = nextStart;

            for(int left = 0; left < counters.length; ++left) {
                nextStart += counters[left];
            }

            while(nextStart < end && !row.get(nextStart)) {
                ++nextStart;
            }
        } while(decodedChar != 42);

        result.deleteCharAt(result.length() - 1);
        if(nextStart != end && row.get(nextStart)) {
            if(result.length() < 2) {
                throw NotFoundException.getNotFoundInstance();
            } else {
                checkChecksums(result);
                result.setLength(result.length() - 2);
                String var14 = decodeExtended(result);
                float var15 = (float)(start[1] + start[0]) / 2.0F;
                float right = (float)(nextStart + lastStart) / 2.0F;
                return new Result(var14, (byte[])null, new ResultPoint[]{new ResultPoint(var15, (float)rowNumber), new ResultPoint(right, (float)rowNumber)}, BarcodeFormat.CODE_93);
            }
        } else {
            throw NotFoundException.getNotFoundInstance();
        }
    }

    private static int[] findAsteriskPattern(BitArray row) throws NotFoundException {
        int width = row.getSize();

        int rowOffset;
        for(rowOffset = 0; rowOffset < width && !row.get(rowOffset); ++rowOffset) {
            ;
        }

        int counterPosition = 0;
        int[] counters = new int[6];
        int patternStart = rowOffset;
        boolean isWhite = false;
        int patternLength = counters.length;

        for(int i = rowOffset; i < width; ++i) {
            boolean pixel = row.get(i);
            if(pixel ^ isWhite) {
                ++counters[counterPosition];
            } else {
                if(counterPosition == patternLength - 1) {
                    if(toPattern(counters) == ASTERISK_ENCODING) {
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

    private static int toPattern(int[] counters) {
        int max = counters.length;
        int sum = 0;

        int pattern;
        for(pattern = 0; pattern < max; ++pattern) {
            sum += counters[pattern];
        }

        pattern = 0;

        for(int i = 0; i < max; ++i) {
            int scaledShifted = (counters[i] << 8) * 9 / sum;
            int scaledUnshifted = scaledShifted >> 8;
            if((scaledShifted & 255) > 127) {
                ++scaledUnshifted;
            }

            if(scaledUnshifted < 1 || scaledUnshifted > 4) {
                return -1;
            }

            if((i & 1) == 0) {
                for(int j = 0; j < scaledUnshifted; ++j) {
                    pattern = pattern << 1 | 1;
                }
            } else {
                pattern <<= scaledUnshifted;
            }
        }

        return pattern;
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
            if(c >= 97 && c <= 100) {
                char next = encoded.charAt(i + 1);
                char decodedChar = 0;
                switch(c) {
                    case 'a':
                        if(next < 65 || next > 90) {
                            throw FormatException.getFormatInstance();
                        }

                        decodedChar = (char)(next - 64);
                        break;
                    case 'b':
                        if(next >= 65 && next <= 69) {
                            decodedChar = (char)(next - 38);
                        } else {
                            if(next < 70 || next > 87) {
                                throw FormatException.getFormatInstance();
                            }

                            decodedChar = (char)(next - 11);
                        }
                        break;
                    case 'c':
                        if(next >= 65 && next <= 79) {
                            decodedChar = (char)(next - 32);
                        } else {
                            if(next != 90) {
                                throw FormatException.getFormatInstance();
                            }

                            decodedChar = 58;
                        }
                        break;
                    case 'd':
                        if(next < 65 || next > 90) {
                            throw FormatException.getFormatInstance();
                        }

                        decodedChar = (char)(next + 32);
                }

                decoded.append(decodedChar);
                ++i;
            } else {
                decoded.append(c);
            }
        }

        return decoded.toString();
    }

    private static void checkChecksums(StringBuffer result) throws ChecksumException {
        int length = result.length();
        checkOneChecksum(result, length - 2, 20);
        checkOneChecksum(result, length - 1, 15);
    }

    private static void checkOneChecksum(StringBuffer result, int checkPosition, int weightMax) throws ChecksumException {
        int weight = 1;
        int total = 0;

        for(int i = checkPosition - 1; i >= 0; --i) {
            total += weight * "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%abcd*".indexOf(result.charAt(i));
            ++weight;
            if(weight > weightMax) {
                weight = 1;
            }
        }

        if(result.charAt(checkPosition) != ALPHABET[total % 47]) {
            throw ChecksumException.getChecksumInstance();
        }
    }

    static {
        ASTERISK_ENCODING = CHARACTER_ENCODINGS[47];
    }
}
