//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.oned;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitArray;
import java.util.Enumeration;
import java.util.Hashtable;

public abstract class OneDReader implements Reader {
    protected static final int INTEGER_MATH_SHIFT = 8;
    protected static final int PATTERN_MATCH_RESULT_SCALE_FACTOR = 256;

    public OneDReader() {
    }

    public Result decode(BinaryBitmap image) throws NotFoundException, FormatException {
        return this.decode(image, (Hashtable)null);
    }

    public Result decode(BinaryBitmap image, Hashtable hints) throws NotFoundException, FormatException {
        try {
            return this.doDecode(image, hints);
        } catch (NotFoundException var12) {
            boolean tryHarder = hints != null && hints.containsKey(DecodeHintType.TRY_HARDER);
            if(tryHarder && image.isRotateSupported()) {
                BinaryBitmap rotatedImage = image.rotateCounterClockwise();
                Result result = this.doDecode(rotatedImage, hints);
                Hashtable metadata = result.getResultMetadata();
                int orientation = 270;
                if(metadata != null && metadata.containsKey(ResultMetadataType.ORIENTATION)) {
                    orientation = (orientation + ((Integer)metadata.get(ResultMetadataType.ORIENTATION)).intValue()) % 360;
                }

                result.putMetadata(ResultMetadataType.ORIENTATION, new Integer(orientation));
                ResultPoint[] points = result.getResultPoints();
                int height = rotatedImage.getHeight();

                for(int i = 0; i < points.length; ++i) {
                    points[i] = new ResultPoint((float)height - points[i].getY() - 1.0F, points[i].getX());
                }

                return result;
            } else {
                throw var12;
            }
        }
    }

    public void reset() {
    }

    private Result doDecode(BinaryBitmap image, Hashtable hints) throws NotFoundException {
        int width = image.getWidth();
        int height = image.getHeight();
        BitArray row = new BitArray(width);
        int middle = height >> 1;
        boolean tryHarder = hints != null && hints.containsKey(DecodeHintType.TRY_HARDER);
        int rowStep = Math.max(1, height >> (tryHarder?8:5));
        int maxLines;
        if(tryHarder) {
            maxLines = height;
        } else {
            maxLines = 15;
        }

        int x = 0;

        while(true) {
            if(x < maxLines) {
                int rowStepsAboveOrBelow = x + 1 >> 1;
                boolean isAbove = (x & 1) == 0;
                int rowNumber = middle + rowStep * (isAbove?rowStepsAboveOrBelow:-rowStepsAboveOrBelow);
                if(rowNumber >= 0 && rowNumber < height) {
                    label99: {
                        try {
                            row = image.getBlackRow(rowNumber, row);
                        } catch (NotFoundException var18) {
                            break label99;
                        }

                        int attempt = 0;

                        while(attempt < 2) {
                            if(attempt == 1) {
                                row.reverse();
                                if(hints != null && hints.containsKey(DecodeHintType.NEED_RESULT_POINT_CALLBACK)) {
                                    Hashtable re = new Hashtable();
                                    Enumeration points = hints.keys();

                                    while(points.hasMoreElements()) {
                                        Object key = points.nextElement();
                                        if(!key.equals(DecodeHintType.NEED_RESULT_POINT_CALLBACK)) {
                                            re.put(key, hints.get(key));
                                        }
                                    }

                                    hints = re;
                                }
                            }

                            try {
                                Result var20 = this.decodeRow(rowNumber, row, hints);
                                if(attempt == 1) {
                                    var20.putMetadata(ResultMetadataType.ORIENTATION, new Integer(180));
                                    ResultPoint[] var21 = var20.getResultPoints();
                                    var21[0] = new ResultPoint((float)width - var21[0].getX() - 1.0F, var21[0].getY());
                                    var21[1] = new ResultPoint((float)width - var21[1].getX() - 1.0F, var21[1].getY());
                                }

                                return var20;
                            } catch (ReaderException var19) {
                                ++attempt;
                            }
                        }
                    }

                    ++x;
                    continue;
                }
            }

            throw NotFoundException.getNotFoundInstance();
        }
    }

    protected static void recordPattern(BitArray row, int start, int[] counters) throws NotFoundException {
        int numCounters = counters.length;

        int end;
        for(end = 0; end < numCounters; ++end) {
            counters[end] = 0;
        }

        end = row.getSize();
        if(start >= end) {
            throw NotFoundException.getNotFoundInstance();
        } else {
            boolean isWhite = !row.get(start);
            int counterPosition = 0;

            int i;
            for(i = start; i < end; ++i) {
                boolean pixel = row.get(i);
                if(pixel ^ isWhite) {
                    ++counters[counterPosition];
                } else {
                    ++counterPosition;
                    if(counterPosition == numCounters) {
                        break;
                    }

                    counters[counterPosition] = 1;
                    isWhite = !isWhite;
                }
            }

            if(counterPosition != numCounters && (counterPosition != numCounters - 1 || i != end)) {
                throw NotFoundException.getNotFoundInstance();
            }
        }
    }

    protected static void recordPatternInReverse(BitArray row, int start, int[] counters) throws NotFoundException {
        int numTransitionsLeft = counters.length;
        boolean last = row.get(start);

        while(start > 0 && numTransitionsLeft >= 0) {
            --start;
            if(row.get(start) != last) {
                --numTransitionsLeft;
                last = !last;
            }
        }

        if(numTransitionsLeft >= 0) {
            throw NotFoundException.getNotFoundInstance();
        } else {
            recordPattern(row, start + 1, counters);
        }
    }

    protected static int patternMatchVariance(int[] counters, int[] pattern, int maxIndividualVariance) {
        int numCounters = counters.length;
        int total = 0;
        int patternLength = 0;

        int unitBarWidth;
        for(unitBarWidth = 0; unitBarWidth < numCounters; ++unitBarWidth) {
            total += counters[unitBarWidth];
            patternLength += pattern[unitBarWidth];
        }

        if(total < patternLength) {
            return 2147483647;
        } else {
            unitBarWidth = (total << 8) / patternLength;
            maxIndividualVariance = maxIndividualVariance * unitBarWidth >> 8;
            int totalVariance = 0;

            for(int x = 0; x < numCounters; ++x) {
                int counter = counters[x] << 8;
                int scaledPattern = pattern[x] * unitBarWidth;
                int variance = counter > scaledPattern?counter - scaledPattern:scaledPattern - counter;
                if(variance > maxIndividualVariance) {
                    return 2147483647;
                }

                totalVariance += variance;
            }

            return totalVariance / total;
        }
    }

    public abstract Result decodeRow(int var1, BitArray var2, Hashtable var3) throws NotFoundException, ChecksumException, FormatException;
}
