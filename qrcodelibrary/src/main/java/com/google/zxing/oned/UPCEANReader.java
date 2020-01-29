//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.common.BitArray;

import java.util.Hashtable;

public abstract class UPCEANReader extends OneDReader {
    private static final int MAX_AVG_VARIANCE = 107;
    private static final int MAX_INDIVIDUAL_VARIANCE = 179;
    static final int[] START_END_PATTERN = new int[]{1, 1, 1};
    static final int[] MIDDLE_PATTERN = new int[]{1, 1, 1, 1, 1};
    static final int[][] L_PATTERNS = new int[][]{{3, 2, 1, 1}, {2, 2, 2, 1}, {2, 1, 2, 2}, {1, 4, 1, 1}, {1, 1, 3, 2}, {1, 2, 3, 1}, {1, 1, 1, 4}, {1, 3, 1, 2}, {1, 2, 1, 3}, {3, 1, 1, 2}};
    static final int[][] L_AND_G_PATTERNS = new int[20][];
    private final StringBuffer decodeRowStringBuffer = new StringBuffer(20);
    private final UPCEANExtensionSupport extensionReader = new UPCEANExtensionSupport();
    private final EANManufacturerOrgSupport eanManSupport = new EANManufacturerOrgSupport();

    protected UPCEANReader() {
    }

    static int[] findStartGuardPattern(BitArray row) throws NotFoundException {
        boolean foundStart = false;
        int[] startRange = null;
        int nextStart = 0;

        while(!foundStart) {
            startRange = findGuardPattern(row, nextStart, false, START_END_PATTERN);
            int start = startRange[0];
            nextStart = startRange[1];
            int quietStart = start - (nextStart - start);
            if(quietStart >= 0) {
                foundStart = row.isRange(quietStart, start, false);
            }
        }

        return startRange;
    }

    public Result decodeRow(int rowNumber, BitArray row, Hashtable hints) throws NotFoundException, ChecksumException, FormatException {
        return this.decodeRow(rowNumber, row, findStartGuardPattern(row), hints);
    }

    public Result decodeRow(int rowNumber, BitArray row, int[] startGuardRange, Hashtable hints) throws NotFoundException, ChecksumException, FormatException {
        ResultPointCallback resultPointCallback = hints == null?null:(ResultPointCallback)hints.get(DecodeHintType.NEED_RESULT_POINT_CALLBACK);
        if(resultPointCallback != null) {
            resultPointCallback.foundPossibleResultPoint(new ResultPoint((float)(startGuardRange[0] + startGuardRange[1]) / 2.0F, (float)rowNumber));
        }

        StringBuffer result = this.decodeRowStringBuffer;
        result.setLength(0);
        int endStart = this.decodeMiddle(row, startGuardRange, result);
        if(resultPointCallback != null) {
            resultPointCallback.foundPossibleResultPoint(new ResultPoint((float)endStart, (float)rowNumber));
        }

        int[] endRange = this.decodeEnd(row, endStart);
        if(resultPointCallback != null) {
            resultPointCallback.foundPossibleResultPoint(new ResultPoint((float)(endRange[0] + endRange[1]) / 2.0F, (float)rowNumber));
        }

        int end = endRange[1];
        int quietEnd = end + (end - endRange[0]);
        if(quietEnd < row.getSize() && row.isRange(end, quietEnd, false)) {
            String resultString = result.toString();
            if(!this.checkChecksum(resultString)) {
                throw ChecksumException.getChecksumInstance();
            } else {
                float left = (float)(startGuardRange[1] + startGuardRange[0]) / 2.0F;
                float right = (float)(endRange[1] + endRange[0]) / 2.0F;
                BarcodeFormat format = this.getBarcodeFormat();
                Result decodeResult = new Result(resultString, (byte[])null, new ResultPoint[]{new ResultPoint(left, (float)rowNumber), new ResultPoint(right, (float)rowNumber)}, format);

                try {
                    Result countryID = this.extensionReader.decodeRow(rowNumber, row, endRange[1]);
                    decodeResult.putAllMetadata(countryID.getResultMetadata());
                    decodeResult.addResultPoints(countryID.getResultPoints());
                } catch (ReaderException var17) {
                    ;
                }

                if(BarcodeFormat.EAN_13.equals(format) || BarcodeFormat.UPC_A.equals(format)) {
                    String countryID1 = this.eanManSupport.lookupCountryIdentifier(resultString);
                    if(countryID1 != null) {
                        decodeResult.putMetadata(ResultMetadataType.POSSIBLE_COUNTRY, countryID1);
                    }
                }

                return decodeResult;
            }
        } else {
            throw NotFoundException.getNotFoundInstance();
        }
    }

    boolean checkChecksum(String s) throws ChecksumException, FormatException {
        return checkStandardUPCEANChecksum(s);
    }

    private static boolean checkStandardUPCEANChecksum(String s) throws FormatException {
        int length = s.length();
        if(length == 0) {
            return false;
        } else {
            int sum = 0;

            int i;
            int digit;
            for(i = length - 2; i >= 0; i -= 2) {
                digit = s.charAt(i) - 48;
                if(digit < 0 || digit > 9) {
                    throw FormatException.getFormatInstance();
                }

                sum += digit;
            }

            sum *= 3;

            for(i = length - 1; i >= 0; i -= 2) {
                digit = s.charAt(i) - 48;
                if(digit < 0 || digit > 9) {
                    throw FormatException.getFormatInstance();
                }

                sum += digit;
            }

            return sum % 10 == 0;
        }
    }

    int[] decodeEnd(BitArray row, int endStart) throws NotFoundException {
        return findGuardPattern(row, endStart, false, START_END_PATTERN);
    }

    static int[] findGuardPattern(BitArray row, int rowOffset, boolean whiteFirst, int[] pattern) throws NotFoundException {
        int patternLength = pattern.length;
        int[] counters = new int[patternLength];
        int width = row.getSize();

        boolean isWhite;
        for(isWhite = false; rowOffset < width; ++rowOffset) {
            isWhite = !row.get(rowOffset);
            if(whiteFirst == isWhite) {
                break;
            }
        }

        int counterPosition = 0;
        int patternStart = rowOffset;

        for(int x = rowOffset; x < width; ++x) {
            boolean pixel = row.get(x);
            if(pixel ^ isWhite) {
                ++counters[counterPosition];
            } else {
                if(counterPosition == patternLength - 1) {
                    if(patternMatchVariance(counters, pattern, 179) < 107) {
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

    static int decodeDigit(BitArray row, int[] counters, int rowOffset, int[][] patterns) throws NotFoundException {
        recordPattern(row, rowOffset, counters);
        int bestVariance = 107;
        int bestMatch = -1;
        int max = patterns.length;

        for(int i = 0; i < max; ++i) {
            int[] pattern = patterns[i];
            int variance = patternMatchVariance(counters, pattern, 179);
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

    abstract BarcodeFormat getBarcodeFormat();

    protected abstract int decodeMiddle(BitArray var1, int[] var2, StringBuffer var3) throws NotFoundException;

    static {
        int i;
        for(i = 0; i < 10; ++i) {
            L_AND_G_PATTERNS[i] = L_PATTERNS[i];
        }

        for(i = 10; i < 20; ++i) {
            int[] widths = L_PATTERNS[i - 10];
            int[] reversedWidths = new int[widths.length];

            for(int j = 0; j < widths.length; ++j) {
                reversedWidths[j] = widths[widths.length - j - 1];
            }

            L_AND_G_PATTERNS[i] = reversedWidths;
        }

    }
}
