//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.common;

import com.google.zxing.Binarizer;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.common.BitArray;
import com.google.zxing.common.BitMatrix;

public class GlobalHistogramBinarizer extends Binarizer {
    private static final int LUMINANCE_BITS = 5;
    private static final int LUMINANCE_SHIFT = 3;
    private static final int LUMINANCE_BUCKETS = 32;
    private byte[] luminances = null;
    private int[] buckets = null;

    public GlobalHistogramBinarizer(LuminanceSource source) {
        super(source);
    }

    public BitArray getBlackRow(int y, BitArray row) throws NotFoundException {
        LuminanceSource source = this.getLuminanceSource();
        int width = source.getWidth();
        if(row != null && row.getSize() >= width) {
            row.clear();
        } else {
            row = new BitArray(width);
        }

        this.initArrays(width);
        byte[] localLuminances = source.getRow(y, this.luminances);
        int[] localBuckets = this.buckets;

        int blackPoint;
        int left;
        for(blackPoint = 0; blackPoint < width; ++blackPoint) {
            left = localLuminances[blackPoint] & 255;
            ++localBuckets[left >> 3];
        }

        blackPoint = estimateBlackPoint(localBuckets);
        left = localLuminances[0] & 255;
        int center = localLuminances[1] & 255;

        for(int x = 1; x < width - 1; ++x) {
            int right = localLuminances[x + 1] & 255;
            int luminance = (center << 2) - left - right >> 1;
            if(luminance < blackPoint) {
                row.set(x);
            }

            left = center;
            center = right;
        }

        return row;
    }

    public BitMatrix getBlackMatrix() throws NotFoundException {
        LuminanceSource source = this.getLuminanceSource();
        int width = source.getWidth();
        int height = source.getHeight();
        BitMatrix matrix = new BitMatrix(width, height);
        this.initArrays(width);
        int[] localBuckets = this.buckets;

        int blackPoint;
        int offset;
        int x;
        int pixel;
        for(blackPoint = 1; blackPoint < 5; ++blackPoint) {
            int localLuminances = height * blackPoint / 5;
            byte[] y = source.getRow(localLuminances, this.luminances);
            offset = (width << 2) / 5;

            for(x = width / 5; x < offset; ++x) {
                pixel = y[x] & 255;
                ++localBuckets[pixel >> 3];
            }
        }

        blackPoint = estimateBlackPoint(localBuckets);
        byte[] var12 = source.getMatrix();

        for(int var13 = 0; var13 < height; ++var13) {
            offset = var13 * width;

            for(x = 0; x < width; ++x) {
                pixel = var12[offset + x] & 255;
                if(pixel < blackPoint) {
                    matrix.set(x, var13);
                }
            }
        }

        return matrix;
    }

    public Binarizer createBinarizer(LuminanceSource source) {
        return new GlobalHistogramBinarizer(source);
    }

    private void initArrays(int luminanceSize) {
        if(this.luminances == null || this.luminances.length < luminanceSize) {
            this.luminances = new byte[luminanceSize];
        }

        if(this.buckets == null) {
            this.buckets = new int[32];
        } else {
            for(int x = 0; x < 32; ++x) {
                this.buckets[x] = 0;
            }
        }

    }

    private static int estimateBlackPoint(int[] buckets) throws NotFoundException {
        int numBuckets = buckets.length;
        int maxBucketCount = 0;
        int firstPeak = 0;
        int firstPeakSize = 0;

        int secondPeak;
        for(secondPeak = 0; secondPeak < numBuckets; ++secondPeak) {
            if(buckets[secondPeak] > firstPeakSize) {
                firstPeak = secondPeak;
                firstPeakSize = buckets[secondPeak];
            }

            if(buckets[secondPeak] > maxBucketCount) {
                maxBucketCount = buckets[secondPeak];
            }
        }

        secondPeak = 0;
        int secondPeakScore = 0;

        int bestValley;
        int bestValleyScore;
        int x;
        for(bestValley = 0; bestValley < numBuckets; ++bestValley) {
            bestValleyScore = bestValley - firstPeak;
            x = buckets[bestValley] * bestValleyScore * bestValleyScore;
            if(x > secondPeakScore) {
                secondPeak = bestValley;
                secondPeakScore = x;
            }
        }

        if(firstPeak > secondPeak) {
            bestValley = firstPeak;
            firstPeak = secondPeak;
            secondPeak = bestValley;
        }

        if(secondPeak - firstPeak <= numBuckets >> 4) {
            throw NotFoundException.getNotFoundInstance();
        } else {
            bestValley = secondPeak - 1;
            bestValleyScore = -1;

            for(x = secondPeak - 1; x > firstPeak; --x) {
                int fromFirst = x - firstPeak;
                int score = fromFirst * fromFirst * (secondPeak - x) * (maxBucketCount - buckets[x]);
                if(score > bestValleyScore) {
                    bestValley = x;
                    bestValleyScore = score;
                }
            }

            return bestValley << 3;
        }
    }
}
